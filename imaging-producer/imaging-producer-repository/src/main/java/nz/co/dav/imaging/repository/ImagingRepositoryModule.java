package nz.co.dav.imaging.repository;

import java.util.Map;

import nz.co.dav.imaging.repository.convert.CypherAPIQuerySuccessRespConverter;
import nz.co.dav.imaging.repository.convert.CypherTransactionalAPIRestSingleTypeRespConverter;
import nz.co.dav.imaging.repository.convert.CypherUpdateStatementReqConverter;
import nz.co.dav.imaging.repository.convert.Neo4jAPIErrorRespConverter;
import nz.co.dav.imaging.repository.convert.Neo4jRestAPIRespJsonConverter;
import nz.co.dav.imaging.repository.convert.NodeAPIRespConverter;
import nz.co.dav.imaging.repository.convert.RelationshipQueryRespConverter;
import nz.co.dav.imaging.repository.convert.RelationshipsQueryRespConverter;
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult;
import nz.co.dav.imaging.repository.support.Neo4jRestAPIAccessor;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class ImagingRepositoryModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Neo4jRestAPIRespJsonConverter.class).asEagerSingleton();
		bind(Neo4jRestAPIAccessor.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	@Named("nodeAPIRespConverter")
	public Function<String, Map<String, String>> nodeAPIRespConverter() {
		return new NodeAPIRespConverter();
	}

	@Provides
	@Singleton
	@Named("relationshipsQueryRespConverter")
	public Function<String, Map<String, Map<String, String>>> relationshipsQueryRespConverter() {
		return new RelationshipsQueryRespConverter();
	}

	@Provides
	@Singleton
	@Named("cypherTransactionalAPIRestSingleTypeRespConverter")
	public Function<String, Map<String, Map<String, String>>> cypherTransactionalAPIRestSingleTypeRespConverter() {
		return new CypherTransactionalAPIRestSingleTypeRespConverter();
	}

	@Provides
	@Singleton
	@Named("relationshipQueryRespConverter")
	public Function<String, Map<String, Object>> relationshipQueryRespConverter() {
		return new RelationshipQueryRespConverter();
	}

	@Provides
	@Named("cypherAPIQuerySuccessRespConverter")
	public Function<String, AbstractCypherQueryResult> cypherAPIQuerySuccessRespConverter() {
		return new CypherAPIQuerySuccessRespConverter();
	}

	@Provides
	@Singleton
	@Named("cypherUpdateStatementReqConverter")
	public Function<Map<String, String>, String> cypherUpdateStatementReqConverter() {
		return new CypherUpdateStatementReqConverter();
	}

	@Provides
	@Singleton
	@Named("neo4jAPIErrorRespConverter")
	public Function<String, String> neo4jAPIErrorRespConverter() {
		return new Neo4jAPIErrorRespConverter();
	}

}
