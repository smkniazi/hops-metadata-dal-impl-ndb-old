package se.sics.hop.metadata.ndb.wrapper;

import com.mysql.clusterj.ClusterJException;
import com.mysql.clusterj.query.Predicate;
import com.mysql.clusterj.query.PredicateOperand;
import com.mysql.clusterj.query.QueryDefinition;
import com.mysql.clusterj.query.QueryDomainType;
import se.sics.hop.exception.StorageException;

public class HopsQueryDomainType<E> {
  private final QueryDomainType<E> queryDomainType;

  public HopsQueryDomainType(QueryDomainType<E> queryDomainType) {
    this.queryDomainType = queryDomainType;
  }

  public HopsPredicateOperand get(String s) throws StorageException {
    try {
      return new HopsPredicateOperand(queryDomainType.get(s));
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public Class<E> getType() throws StorageException {
    try {
      return queryDomainType.getType();
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public HopsQueryDefinition<E> where(HopsPredicate predicate)
      throws StorageException {
    try {
      return new HopsQueryDefinition<E>(queryDomainType.where(predicate.getPredicate()));
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public HopsPredicateOperand param(String s) throws StorageException {
    try {
      return new HopsPredicateOperand(queryDomainType.param(s));
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public HopsPredicate not(HopsPredicate predicate) throws StorageException {
    try {
      return new HopsPredicate(queryDomainType.not(predicate.getPredicate()));
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  QueryDomainType<E> getQueryDomainType() {
    return queryDomainType;
  }
}
