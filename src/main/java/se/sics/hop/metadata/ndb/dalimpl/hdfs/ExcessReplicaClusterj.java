package se.sics.hop.metadata.ndb.dalimpl.hdfs;

import com.google.common.primitives.Ints;
import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.Index;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import com.mysql.clusterj.query.Predicate;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import se.sics.hop.metadata.hdfs.entity.hop.HopExcessReplica;
import se.sics.hop.metadata.hdfs.dal.ExcessReplicaDataAccess;
import se.sics.hop.exception.StorageException;
import se.sics.hop.metadata.ndb.ClusterjConnector;
import se.sics.hop.metadata.ndb.mysqlserver.CountHelper;
import se.sics.hop.metadata.hdfs.tabledef.ExcessReplicaTableDef;

/**
 *
 * @author Hooman <hooman@sics.se>
 */
public class ExcessReplicaClusterj implements ExcessReplicaTableDef, ExcessReplicaDataAccess<HopExcessReplica> {



  @PersistenceCapable(table = TABLE_NAME)
  public interface ExcessReplicaDTO {

    @PrimaryKey
    @Column(name = INODE_ID)
    int getINodeId();
    void setINodeId(int inodeID);
    
    @PrimaryKey
    @Column(name = BLOCK_ID)
    long getBlockId();
    void setBlockId(long storageId);

    @PrimaryKey
    @Column(name = STORAGE_ID)
    @Index(name = STORAGE_IDX)
    int getStorageId();
    void setStorageId(int storageId);
  }
  private ClusterjConnector connector = ClusterjConnector.getInstance();

  @Override
  public int countAll() throws StorageException {
    return CountHelper.countAll(TABLE_NAME);
  }

  @Override
  public void prepare(Collection<HopExcessReplica> removed, Collection<HopExcessReplica> newed, Collection<HopExcessReplica> modified) throws StorageException {
    try {
      Session session = connector.obtainSession();
      List<ExcessReplicaDTO> changes = new ArrayList<ExcessReplicaDTO>();
      List<ExcessReplicaDTO> deletions = new ArrayList<ExcessReplicaDTO>();
      for (HopExcessReplica exReplica : newed) {
        ExcessReplicaDTO newInstance = session.newInstance(ExcessReplicaDTO.class);
        createPersistable(exReplica, newInstance);
        changes.add(newInstance);
      }

      for (HopExcessReplica exReplica : removed) {
        ExcessReplicaDTO newInstance = session.newInstance(ExcessReplicaDTO.class);
        createPersistable(exReplica, newInstance);
        deletions.add(newInstance);
      }
      session.deletePersistentAll(deletions);
      session.savePersistentAll(changes);
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

  @Override
  public List<HopExcessReplica> findExcessReplicaByStorageId(int storageId) throws StorageException {
    try {
      Session session = connector.obtainSession();
      QueryBuilder qb = session.getQueryBuilder();
      QueryDomainType<ExcessReplicaDTO> qdt = qb.createQueryDefinition(ExcessReplicaDTO.class);
      qdt.where(qdt.get("storageId").equal(qdt.param("param")));
      Query<ExcessReplicaDTO> query = session.createQuery(qdt);
      query.setParameter("param", storageId);
      return createList(query.getResultList());
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

  @Override
  public List<HopExcessReplica> findExcessReplicaByBlockId(long bId, int inodeId) throws StorageException {
    try {
      Session session = connector.obtainSession();
      QueryBuilder qb = session.getQueryBuilder();
      QueryDomainType<ExcessReplicaDTO> qdt = qb.createQueryDefinition(ExcessReplicaDTO.class);
      Predicate pred1 = qdt.get("blockId").equal(qdt.param("blockIdParam"));
      Predicate pred2 = qdt.get("iNodeId").equal(qdt.param("iNodeIdParam"));
      qdt.where(pred1.and(pred2));
      Query<ExcessReplicaDTO> query = session.createQuery(qdt);
      query.setParameter("blockIdParam", bId);
      query.setParameter("iNodeIdParam", inodeId);
      return createList(query.getResultList());
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }
  
    @Override
  public List<HopExcessReplica> findExcessReplicaByINodeId(int inodeId) throws StorageException {
    try {
      Session session = connector.obtainSession();
      QueryBuilder qb = session.getQueryBuilder();
      QueryDomainType<ExcessReplicaDTO> qdt = qb.createQueryDefinition(ExcessReplicaDTO.class);
      Predicate pred1 = qdt.get("iNodeId").equal(qdt.param("iNodeIdParam"));
      qdt.where(pred1);
      Query<ExcessReplicaDTO> query = session.createQuery(qdt);
      query.setParameter("iNodeIdParam", inodeId);
      return createList(query.getResultList());
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

  @Override
  public List<HopExcessReplica> findExcessReplicaByINodeIds(int[] inodeIds) throws StorageException {
    try {
      Session session = connector.obtainSession();
      QueryBuilder qb = session.getQueryBuilder();
      QueryDomainType<ExcessReplicaDTO> qdt = qb.createQueryDefinition(ExcessReplicaDTO.class);
      Predicate pred1 = qdt.get("iNodeId").in(qdt.param("iNodeIdParam"));
      qdt.where(pred1);
      Query<ExcessReplicaDTO> query = session.createQuery(qdt);
      query.setParameter("iNodeIdParam", Ints.asList(inodeIds));
      return createList(query.getResultList());
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }
      
  @Override
  public HopExcessReplica findByPK(long bId, int sId, int inodeId) throws StorageException {
    try {
      Session session = connector.obtainSession();
      Object[] pk = new Object[4];
      pk[0] = inodeId;
      pk[1] = bId;
      pk[2] = sId;

      ExcessReplicaDTO invTable = session.find(ExcessReplicaDTO.class, pk);
      if (invTable == null) {
        return null;
      }
      HopExcessReplica result = createReplica(invTable);
      return result;
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

  @Override
  public void removeAll() throws StorageException {
    try {
      Session session = connector.obtainSession();
      session.deletePersistentAll(ExcessReplicaDTO.class);
    } catch (Exception e) {
      throw new StorageException(e);
    }
  }

  private List<HopExcessReplica> createList(List<ExcessReplicaDTO> list) {
    List<HopExcessReplica> result = new ArrayList<HopExcessReplica>();
    for (ExcessReplicaDTO item : list) {
      result.add(createReplica(item));
    }
    return result;
  }

  private HopExcessReplica createReplica(ExcessReplicaDTO exReplicaTable) {
    return new HopExcessReplica(exReplicaTable.getStorageId(), exReplicaTable.getBlockId(), exReplicaTable.getINodeId());
  }

  private void createPersistable(HopExcessReplica exReplica, ExcessReplicaDTO exReplicaTable) {
    exReplicaTable.setBlockId(exReplica.getBlockId());
    exReplicaTable.setStorageId(exReplica.getStorageId());
    exReplicaTable.setINodeId(exReplica.getInodeId());
  }
}
