package com.heer.yjs.ds.common.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.heer.yjs.ds.common.annotations.PK;
import com.heer.yjs.ds.common.annotations.Table;
import com.heer.yjs.ds.common.utils.Java2JDBCTypeUtils;
import com.heer.yjs.ds.common.utils.Reflections;

/**
 * CRUD SQL 注入器
 */
public class CrudSqlInjector {

	private transient Logger logger = LoggerFactory.getLogger(getClass());

	private static final String DEFAULT_PK = "id";

	private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = #{PK}";
	private static final String SQL_SELECTONE = "SELECT * FROM %s  WHERE %s = #{PK}";
	private static final String SQL_SELECTALL = "SELECT * FROM %s";

	private static final String METHOD_INSERTONE = "insertOne";
	private static final String METHOD_UPDATEONE = "updateByPK";
	private static final String METHOD_UPDATEIGNOREEMPTY = "updateIgnoreEmpty";
	private static final String METHOD_DELETEONE = "deleteByPK";
	private static final String METHOD_SELECTONE = "selectByPK";
	private static final String METHOD_SELECTALL = "selectAll";
	private static final String METHOD_SELECTBYCON = "selectByCon";
	private static final String METHOD_SELECTBYSQL = "selectBySql";
	
	
	private static final String ORDERBY = "orderBy";
	private static final String WHERESQL = "whereSql"; 
	

	private Configuration configuration;
	private MapperBuilderAssistant assistant;
	private TPK tablePK;// 数据库主键

	public CrudSqlInjector(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public void inject(Class<?> mapperClass) {
		assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
		assistant.setCurrentNamespace(mapperClass.getName());

		Class<?> modelClass = extractModelClass(mapperClass);
		String table = this.extractTableName(modelClass);
		tablePK = this.extractTablePK(modelClass);
		System.out.println("*************注册表："+table +",其主键为："+tablePK.name+"*************" );

		// 新增
		this.injectInsertSql(mapperClass, modelClass, table);

		// 没有指定主键，默认忽略按主键修改、删除、查询方法
		if (tablePK != null) {
			// 根据主键修改，主键名默认为id
			this.injectUpdateSql(mapperClass, modelClass, table);
			
			// 根据主键修改，忽略空字段
			this.injectUpdateIgnoreEmpty(mapperClass, modelClass, table); 

			// 根据主键删除，主键名默认为id
			SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_DELETE, table, tablePK.name), Object.class);
			this.addMappedStatement(mapperClass, METHOD_DELETEONE, sqlSource, SqlCommandType.DELETE, null);


			// 根据主键查找，主键名默认为id
			sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTONE, table, tablePK.name), Object.class);
			this.addMappedStatement(mapperClass, METHOD_SELECTONE, sqlSource, SqlCommandType.SELECT, modelClass);
		}

		// 查询全部
		SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTALL, table), null);
		this.addMappedStatement(mapperClass, METHOD_SELECTALL, sqlSource, SqlCommandType.SELECT, modelClass);
		
		//条件查询（模糊）
		this.injectSelectByCon(mapperClass, modelClass, table);
		
		//传入Sql查询
		this.injectSelectBySql(mapperClass, modelClass, table);
		
	}

	private Class<?> extractModelClass(Class<?> mapperClass) {
		Type[] types = mapperClass.getGenericInterfaces();
		ParameterizedType target = null;
		for (Type type : types) {
			if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(CrudMapper.class)) {
				target = (ParameterizedType) type;
				break;
			}
		}
		Type[] parameters = target.getActualTypeArguments();
		Class<?> modelClass = (Class<?>) parameters[0];
		return modelClass;
	}

	/** 提取数据库表名 **/
	private String extractTableName(Class<?> modelClass) {
		Table table = modelClass.getAnnotation(Table.class);
		if (table != null && table.name() != null && table.name().trim().length() > 0) {
			return table.name();
		}

		return this.camelToUnderline(modelClass.getSimpleName());
	}

	/** 提取主键 */
	private TPK extractTablePK(Class<?> modelClass) {
		List<Field> fields = Reflections.getAllFieldsExcludeTransient(modelClass);
		TPK TPK = null;
		for (Field field : fields) {
			PK PK = field.getAnnotation(PK.class);
			if (PK != null) {
				TPK = new TPK();
				TPK.name = field.getName();
				TPK.auto = PK.auto();
				break;
			}
		}
		
		if (TPK != null) {
			return TPK;
		}

		// 检测是否采用了默认id，作为主键
		for (Field field : fields) {
			if (this.isDefaultAutoPK(field)) {
				TPK = new TPK();
				TPK.name = field.getName();
				TPK.auto = true;
				return TPK;
			}
		}

		return null;
	}

	private void injectInsertSql(Class<?> mapperClass, Class<?> modelClass, String table) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		String keyProperty = null;
		String keyColumn = null;

		List<Field> fields = Reflections.getAllFieldsExcludeTransient(modelClass);
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();

		int fieldSize = fields.size();
		for (int i = 0; i < fieldSize; i++) {
			String fieldName = fields.get(i).getName();
			if (tablePK != null && tablePK.name.equals(fieldName) && tablePK.auto) {
				keyGenerator = new Jdbc3KeyGenerator();
				keyProperty = keyColumn = tablePK.name;
				continue;
			}
			if (ORDERBY.equals(fieldName)||WHERESQL.equals(fieldName)) {//过滤掉orderby和wheresql
				continue;
			}
			fieldBuilder.append(fieldName);
			placeholderBuilder.append("#{" + fieldName + ",jdbcType="+Java2JDBCTypeUtils.getJdbcType(fields.get(i))+"}");
			fieldBuilder.append(",");
			placeholderBuilder.append(",");
		}
		String fieldString = fieldBuilder.toString().substring(0,fieldBuilder.toString().length()-1);
		String placeholderString = placeholderBuilder.toString().substring(0,placeholderBuilder.toString().length()-1);
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", table,fieldString , placeholderString);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, METHOD_INSERTONE, sqlSource, keyGenerator, keyProperty, keyColumn);
	}

	private void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, String table) {
		List<Field> fields = Reflections.getAllFieldsExcludeTransient(modelClass);
		StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");

		int fieldSize = fields.size();
		for (int i = 0; i < fieldSize; i++) {
			String fieldName = fields.get(i).getName();
			if (tablePK.name.equals(fieldName)||ORDERBY.equals(fieldName)||WHERESQL.equals(fieldName)) {
				continue;
			}

			sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
			if (i < fieldSize - 1) {
				sqlBuilder.append(", ");
			}
		}
		sqlBuilder.append(" WHERE ").append(tablePK.name).append("= #{").append(tablePK.name).append("}");
		SqlSource sqlSource = new RawSqlSource(configuration, sqlBuilder.toString(), modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, METHOD_UPDATEONE, sqlSource);
	}
	/**
	 * 
	 * 使用动态sql拼接判断非空字段更新
	 * auther:lint
	 * 
	 * */
	private void injectUpdateIgnoreEmpty(Class<?> mapperClass, Class<?> modelClass, String table) {
		List<Field> fields = Reflections.getAllFieldsExcludeTransient(modelClass);
		StringBuilder sqlBuilder = new StringBuilder("<script> UPDATE ").append(table).append(" <set> ");

		int fieldSize = fields.size();
		for (int i = 0; i < fieldSize; i++) {
			String fieldName = fields.get(i).getName();
			if (tablePK.name.equals(fieldName)||ORDERBY.equals(fieldName)||WHERESQL.equals(fieldName)) {
				continue;
			}
			sqlBuilder.append("<if test=\"").append(fieldName).append(" != null and ").append(fieldName).append(" !=''\">");
			sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
			sqlBuilder.append(", ");
			sqlBuilder.append("</if>");
		}
		sqlBuilder.append("</set>");
		sqlBuilder.append(" WHERE ").append(tablePK.name).append("=#{").append(tablePK.name).append("} </script>");
		SqlSource sqlSource = new XMLLanguageDriver().createSqlSource(configuration, sqlBuilder.toString(), modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, METHOD_UPDATEIGNOREEMPTY, sqlSource);
	}

	/**
	 * @auther:lint
	 * @注释: 条件模糊查询
	 * @createTime: 2016年10月25日
	 * 
	 * */
	private void injectSelectByCon(Class<?> mapperClass, Class<?> modelClass, String table) {
		List<Field> fields = Reflections.getAllFieldsExcludeTransient(modelClass);
		StringBuilder sqlBuilder = new StringBuilder("<script> select * from ").append(table).append(" <where> ");
		for (Field field : fields) {
			String fieldName = field.getName();
			if(ORDERBY.equals(fieldName)||WHERESQL.equals(fieldName)) continue;
			sqlBuilder.append("<if test=\"").append(fieldName).append(" != null and ").append(fieldName).append(" !=''\">");
			sqlBuilder.append(" and ").append(fieldName).append(" like \"%\"#{").append(fieldName).append("}\"%\"");
			sqlBuilder.append("</if>");
		}
		sqlBuilder.append("</where>");
		sqlBuilder.append("<if test=\"orderBy!=null and orderBy !=''\"> order by ${orderBy}</if>");
		sqlBuilder.append("</script>");
		
		SqlSource sqlSource = new XMLLanguageDriver().createSqlSource(configuration, sqlBuilder.toString(), modelClass);
		this.addMappedStatement(mapperClass, METHOD_SELECTBYCON, sqlSource, SqlCommandType.SELECT, modelClass);
	}
	/**
	 * @auther:Lint
	 * @createTime: 2016年10月25日
	 * @注释:通过传入的whereSql查询
	**/
	private void injectSelectBySql(Class<?> mapperClass, Class<?> modelClass, String table) {
		StringBuilder sqlBuilder = new StringBuilder("<script> select * from ").append(table).append(" <where> ");
		sqlBuilder.append(" ${whereSql} </where>");
		sqlBuilder.append("<if test=\"orderBy!=null and orderBy !=''\"> order by ${orderBy} </if>");
		sqlBuilder.append("</script>");
		SqlSource sqlSource = new XMLLanguageDriver().createSqlSource(configuration, sqlBuilder.toString(), modelClass);
		this.addMappedStatement(mapperClass, METHOD_SELECTBYSQL, sqlSource, SqlCommandType.SELECT, modelClass);
	}
	
	private void addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> resultType) {
		this.addMappedStatement(mapperClass, id, sqlSource, sqlCommandType, null, resultType, new NoKeyGenerator(), null, null);
	}

	private void addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, keyGenerator, keyProperty, keyColumn);
	}

	private void addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
		this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, new NoKeyGenerator(), null, null);
	}

	private void addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator,
			String keyProperty, String keyColumn) {
		String statementName = mapperClass.getName() + "." + id;
		if (configuration.hasStatement(statementName)) { 
			logger.warn("{},已通过xml或SqlProvider加载了，忽略该sql的注入", statementName);
			return;
		}
		assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null, parameterClass, null, resultType, null, false, true, false, keyGenerator, keyProperty,
				keyColumn, configuration.getDatabaseId(), new XMLLanguageDriver(), null);
	}

	private boolean isDefaultAutoPK(Field field) {
		String fieldName = field.getName();
		if (DEFAULT_PK.equals(fieldName)) {
			Class<?> fieldClass = field.getType();
			if (fieldClass.equals(long.class) || fieldClass.equals(Long.class) || fieldClass.equals(int.class) || fieldClass.equals(Integer.class)) {// 猜测为id主键自增
				return true;
			}
		}
		return false;
	}

	private String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	/**
	 * Table PK
	 */
	class TPK {
		String name;
		boolean auto;
	}
}
