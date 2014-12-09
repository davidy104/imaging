package nz.co.dav.imaging.repository;

import groovy.json.JsonSlurper;

import java.util.Map;
import java.util.Set;

import nz.co.dav.imaging.model.ImageMetaModel;
import nz.co.dav.imaging.repository.convert.CypherAPIQuerySuccessRespConverter;
import nz.co.dav.imaging.repository.convert.CypherCreateStatementReqConverter;
import nz.co.dav.imaging.repository.convert.CypherInPredicateConverter;
import nz.co.dav.imaging.repository.convert.CypherTransactionalAPIRestSingleTypeRespConverter;
import nz.co.dav.imaging.repository.convert.CypherUpdateStatementReqConverter;
import nz.co.dav.imaging.repository.convert.ImageMetaMapToModelConverter;
import nz.co.dav.imaging.repository.convert.Neo4jAPIErrorRespConverter;
import nz.co.dav.imaging.repository.convert.Neo4jRestAPIRespJsonConverter;
import nz.co.dav.imaging.repository.convert.NodeAPIRespConverter;
import nz.co.dav.imaging.repository.convert.RelationshipQueryRespConverter;
import nz.co.dav.imaging.repository.convert.RelationshipsQueryRespConverter;
import nz.co.dav.imaging.repository.impl.ImagingMetaDataRepositoryImpl;
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
		bind(ImagingMetaDataRepository.class).to(ImagingMetaDataRepositoryImpl.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	@Named("nodeAPIRespConverter")
	public Function<String, Map<String, String>> nodeAPIRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new NodeAPIRespConverter(jsonSlurper);
	}

	@Provides
	@Singleton
	@Named("relationshipsQueryRespConverter")
	public Function<String, Map<String, Map<String, String>>> relationshipsQueryRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new RelationshipsQueryRespConverter(jsonSlurper);
	}

	@Provides
	@Singleton
	@Named("cypherTransactionalAPIRestSingleTypeRespConverter")
	public Function<String, Map<String, Map<String, String>>> cypherTransactionalAPIRestSingleTypeRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new CypherTransactionalAPIRestSingleTypeRespConverter(jsonSlurper);
	}

	@Provides
	@Singleton
	@Named("relationshipQueryRespConverter")
	public Function<String, Map<String, Object>> relationshipQueryRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new RelationshipQueryRespConverter(jsonSlurper);
	}

	@Provides
	@Named("cypherAPIQuerySuccessRespConverter")
	public Function<String, AbstractCypherQueryResult> cypherAPIQuerySuccessRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new CypherAPIQuerySuccessRespConverter(jsonSlurper);
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
	public Function<String, String> neo4jAPIErrorRespConverter(final @Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new Neo4jAPIErrorRespConverter(jsonSlurper);
	}

	@Provides
	@Singleton
	@Named("imageMetaMapToModelConverter")
	public Function<Map<String, String>, ImageMetaModel> imageMetaMapToModelConverter() {
		return new ImageMetaMapToModelConverter();
	}

	@Provides
	@Singleton
	@Named("cypherCreateStatmentReqConverter")
	public Function<Map<String, Object>, String> cypherCreateStatmentReqConverter() {
		return new CypherCreateStatementReqConverter();
	}

	@Provides
	@Singleton
	@Named("cypherInPredicateConverter")
	public Function<Set<String>, String> cypherInPredicateConverter() {
		return new CypherInPredicateConverter();
	}

}
