package com.gregor_lohaus.gtransfer.native_image;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class HibernateRuntimeHints implements RuntimeHintsRegistrar {
  private static final String[] LOGGER_IMPLEMENTATIONS = {
      "org.hibernate.jpa.internal.JpaLogger_$logger",
      "org.hibernate.internal.CoreMessageLogger_$logger",
      "org.hibernate.internal.log.DeprecationLogger_$logger",
      "org.hibernate.internal.log.IncubationLogger_$logger",
      "org.hibernate.internal.log.ConnectionAccessLogger_$logger",
      "org.hibernate.internal.log.ConnectionInfoLogger_$logger",
      "org.hibernate.internal.log.StatisticsLogger_$logger",
      "org.hibernate.internal.log.UrlMessageBundle_$logger",
      "org.hibernate.internal.SessionFactoryLogging_$logger",
      "org.hibernate.internal.SessionFactoryRegistryMessageLogger_$logger",
      "org.hibernate.internal.SessionLogging_$logger",
      "org.hibernate.boot.BootLogging_$logger",
      "org.hibernate.boot.archive.scan.internal.ScannerLogger_$logger",
      "org.hibernate.boot.beanvalidation.BeanValidationLogger_$logger",
      "org.hibernate.boot.jaxb.JaxbLogger_$logger",
      "org.hibernate.dialect.DialectLogging_$logger",
      "org.hibernate.engine.jdbc.JdbcLogging_$logger",
      "org.hibernate.engine.jdbc.batch.JdbcBatchLogging_$logger",
      "org.hibernate.engine.jdbc.connections.internal.ConnectionProviderLogging_$logger",
      "org.hibernate.engine.jdbc.env.internal.LobCreationLogging_$logger",
      "org.hibernate.engine.jdbc.spi.SQLExceptionLogging_$logger",
      "org.hibernate.engine.internal.NaturalIdLogging_$logger",
      "org.hibernate.engine.internal.PersistenceContextLogging_$logger",
      "org.hibernate.engine.internal.SessionMetricsLogger_$logger",
      "org.hibernate.engine.internal.VersionLogger_$logger",
      "org.hibernate.resource.jdbc.internal.LogicalConnectionLogging_$logger",
      "org.hibernate.resource.jdbc.internal.ResourceRegistryLogger_$logger",
      "org.hibernate.resource.transaction.internal.SynchronizationLogging_$logger",
      "org.hibernate.resource.transaction.backend.jta.internal.JtaLogging_$logger",
      "org.hibernate.resource.beans.internal.BeansMessageLogger_$logger",
      "org.hibernate.service.internal.ServiceLogger_$logger",
      "org.hibernate.sql.ast.tree.SqlAstTreeLogger_$logger",
      "org.hibernate.sql.exec.SqlExecLogger_$logger",
      "org.hibernate.sql.model.ModelMutationLogging_$logger",
      "org.hibernate.sql.results.LoadingLogger_$logger",
      "org.hibernate.sql.results.ResultsLogger_$logger",
      "org.hibernate.sql.results.graph.embeddable.EmbeddableLoadingLogger_$logger",
      "org.hibernate.query.QueryLogging_$logger",
      "org.hibernate.query.hql.HqlLogging_$logger",
      "org.hibernate.id.UUIDLogger_$logger",
      "org.hibernate.id.enhanced.OptimizerLogger_$logger",
      "org.hibernate.id.enhanced.SequenceGeneratorLogger_$logger",
      "org.hibernate.id.enhanced.TableGeneratorLogger_$logger",
      "org.hibernate.action.internal.ActionLogging_$logger",
      "org.hibernate.cache.spi.SecondLevelCacheLogger_$logger",
      "org.hibernate.collection.internal.CollectionLogger_$logger",
      "org.hibernate.context.internal.CurrentSessionLogging_$logger",
      "org.hibernate.event.internal.EntityCopyLogging_$logger",
      "org.hibernate.event.internal.EventListenerLogging_$logger",
      "org.hibernate.loader.ast.internal.MultiKeyLoadLogging_$logger",
      "org.hibernate.metamodel.mapping.MappingModelCreationLogging_$logger",
      "org.hibernate.bytecode.enhance.internal.BytecodeEnhancementLogging_$logger",
      "org.hibernate.bytecode.enhance.spi.interceptor.BytecodeInterceptorLogging_$logger",
  };

  private static final Class<?>[] EVENT_LISTENER_TYPES = {
      org.hibernate.event.spi.AutoFlushEventListener.class,
      org.hibernate.event.spi.ClearEventListener.class,
      org.hibernate.event.spi.DeleteEventListener.class,
      org.hibernate.event.spi.DirtyCheckEventListener.class,
      org.hibernate.event.spi.EvictEventListener.class,
      org.hibernate.event.spi.FlushEntityEventListener.class,
      org.hibernate.event.spi.FlushEventListener.class,
      org.hibernate.event.spi.InitializeCollectionEventListener.class,
      org.hibernate.event.spi.LoadEventListener.class,
      org.hibernate.event.spi.LockEventListener.class,
      org.hibernate.event.spi.MergeEventListener.class,
      org.hibernate.event.spi.PersistEventListener.class,
      org.hibernate.event.spi.PostCollectionRecreateEventListener.class,
      org.hibernate.event.spi.PostCollectionRemoveEventListener.class,
      org.hibernate.event.spi.PostCollectionUpdateEventListener.class,
      org.hibernate.event.spi.PostCommitDeleteEventListener.class,
      org.hibernate.event.spi.PostCommitInsertEventListener.class,
      org.hibernate.event.spi.PostCommitUpdateEventListener.class,
      org.hibernate.event.spi.PostDeleteEventListener.class,
      org.hibernate.event.spi.PostInsertEventListener.class,
      org.hibernate.event.spi.PostLoadEventListener.class,
      org.hibernate.event.spi.PostUpdateEventListener.class,
      org.hibernate.event.spi.PostUpsertEventListener.class,
      org.hibernate.event.spi.PreCollectionRecreateEventListener.class,
      org.hibernate.event.spi.PreCollectionRemoveEventListener.class,
      org.hibernate.event.spi.PreCollectionUpdateEventListener.class,
      org.hibernate.event.spi.PreDeleteEventListener.class,
      org.hibernate.event.spi.PreFlushEventListener.class,
      org.hibernate.event.spi.PreInsertEventListener.class,
      org.hibernate.event.spi.PreLoadEventListener.class,
      org.hibernate.event.spi.PreUpdateEventListener.class,
      org.hibernate.event.spi.PreUpsertEventListener.class,
      org.hibernate.event.spi.RefreshEventListener.class,
      org.hibernate.event.spi.ReplicateEventListener.class,
  };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    for (String logger : LOGGER_IMPLEMENTATIONS) {
      hints.reflection().registerTypeIfPresent(classLoader, logger,
          MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
          MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
          MemberCategory.INVOKE_DECLARED_METHODS,
          MemberCategory.INVOKE_PUBLIC_METHODS);
    }

    for (Class<?> listenerType : EVENT_LISTENER_TYPES) {
      hints.reflection().registerType(
          listenerType.arrayType(),
          MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    }

    hints.reflection().registerTypeIfPresent(classLoader,
        "org.hibernate.event.spi.PostActionEventListener[]",
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
  }
}
