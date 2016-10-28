package com.heer.yjs.ds.common.mybatis;

import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heer.yjs.ds.common.persistence.CrudMapper;
import com.heer.yjs.ds.common.persistence.CrudSqlInjector;

public class MybatisConfiguration extends Configuration {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Mybatis加载sql的顺序：<br>
	 * 1、加载xml中的sql；<br>
	 * 2、加载sqlprovider中的sql<br>
	 * 3、xmlsql 与 sqlprovider不能包含相同的sql <br>
	 * <br>
	 * 调整后的sql优先级：xmlsql > sqlprovider > crudsql
	 */
	@Override
	public void addMappedStatement(MappedStatement ms) {
		if (this.mappedStatements.containsKey(ms.getId())) {// 说明已加载了xml中的节点；忽略mapper中的SqlProvider数据
			logger.warn("mapper[{}] is ignored, because it's exists, maybe from xml file", ms.getId());
			return;
		}
		super.addMappedStatement(ms);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return super.getMapper(type, sqlSession);
	}

	// 复写addMapper方法
	@Override
	public <T> void addMapper(Class<T> type) {
		super.addMapper(type); 

		if (!CrudMapper.class.isAssignableFrom(type)) {
			return;
		}
		new CrudSqlInjector(this).inject(type);
	}

	@Override
	public void addMappers(String packageName) {
		this.addMappers(packageName, Object.class);
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
		System.out.println("*************start inject table*************");
		for (Class<?> mapperClass : mapperSet) {
			this.addMapper(mapperClass);
		}
		System.out.println("**************end inject table**************");
	}
}
