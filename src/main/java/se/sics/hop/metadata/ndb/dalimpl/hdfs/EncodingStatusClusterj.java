package se.sics.hop.metadata.ndb.dalimpl.hdfs;

import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.Index;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.sics.hop.exception.StorageException;
import se.sics.hop.metadata.hdfs.dal.EncodingStatusDataAccess;
import se.sics.hop.metadata.hdfs.entity.hop.HopEncodingStatus;
import se.sics.hop.metadata.hdfs.tabledef.EncodingStatusTableDef;
import se.sics.hop.metadata.ndb.ClusterjConnector;
import se.sics.hop.metadata.ndb.mysqlserver.CountHelper;
import se.sics.hop.metadata.ndb.mysqlserver.HopsSQLExceptionHelper;
import se.sics.hop.metadata.ndb.mysqlserver.MysqlServerConnector;
import se.sics.hop.metadata.ndb.wrapper.HopsQuery;
import se.sics.hop.metadata.ndb.wrapper.HopsQueryBuilder;
import se.sics.hop.metadata.ndb.wrapper.HopsQueryDomainType;
import se.sics.hop.metadata.ndb.wrapper.HopsSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EncodingStatusClusterj implements EncodingStatusTableDef, EncodingStatusDataAccess<HopEncodingStatus> {

  static final Log LOG = LogFactory.getLog(EncodingStatusClusterj.class);

  private ClusterjConnector clusterjConnector = ClusterjConnector.getInstance();
  private MysqlServerConnector mysqlConnector = MysqlServerConnector.getInstance();

  @PersistenceCapable(table = TABLE_NAME)
  public interface EncodingStatusDto {

    @PrimaryKey
    @Column(name = INODE_ID)
    int getInodeId();

    void setInodeId(int inodeId);

    @Column(name = STATUS)
    Integer getStatus();

    void setStatus(Integer status);

    @Column(name = CODEC)
    String getCodec();

    void setCodec(String codec);

    @Column(name = TARGET_REPLICATION)
    Short getTargetReplication();

    void setTargetReplication(Short targetReplication);

    @Column(name = STATUS_MODIFICATION_TIME)
    Long getStatusModificationTime();

    void setStatusModificationTime(Long modificationTime);

    @Index
    @Column(name = PARITY_INODE_ID)
    int getParityInodeId();

    // Long type not possible because of index
    void setParityInodeId(int inodeId);

    @Column(name = PARITY_STATUS)
    Integer getParityStatus();

    void setParityStatus(Integer status);

    @Column(name = PARITY_STATUS_MODIFICATION_TIME)
    Long getParityStatusModificationTime();

    void setParityStatusModificationTime(Long modificationTime);

    @Column(name = PARITY_FILE_NAME)
    String getParityFileName();

    void setParityFileName(String parityFileName);

    @Column(name = LOST_BLOCKS)
    int getLostBlockCount();

    void setLostBlockCount(int n);

    @Column(name = LOST_PARITY_BLOCKS)
    int getLostParityBlockCount();

    void setLostParityBlockCount(int n);

    @Column(name = REVOKED)
    Boolean getRevoked();

    void setRevoked(Boolean revoked);
  }

  @Override
  public void add(HopEncodingStatus status) throws StorageException {
    LOG.info("ADD " + status.toString());
    HopsSession session = clusterjConnector.obtainSession();
    EncodingStatusDto dto = session.newInstance(EncodingStatusDto.class);
    copyState(status, dto);
    session.savePersistent(dto);
  }

  @Override
  public void update(HopEncodingStatus status) throws StorageException {
    LOG.info("UPDATE " + status.toString());
    HopsSession session = clusterjConnector.obtainSession();
    EncodingStatusDto dto = session.newInstance(EncodingStatusDto.class);
    copyState(status, dto);
    session.updatePersistent(dto);
  }

  @Override
  public void delete(HopEncodingStatus status) throws StorageException {
    HopsSession session = clusterjConnector.obtainSession();
    EncodingStatusDto dto = session.newInstance(EncodingStatusDto.class);
    copyState(status, dto);
    LOG.info("Delte " + status);
    session.deletePersistent(dto);
  }

  private void copyState(HopEncodingStatus status, EncodingStatusDto dto) {
    Integer inodeId = status.getInodeId();
    if (inodeId != null) {
      dto.setInodeId(inodeId);
    }
    Integer sourceStatus = status.getStatus();
    if (sourceStatus != null) {
      dto.setStatus(sourceStatus);
    }
    String codec = status.getCodec();
    if (codec != null) {
      dto.setCodec(codec);
    }
    Short targetReplication = status.getTargetReplication();
    if (targetReplication != null) {
      dto.setTargetReplication(targetReplication);
    }
    Long statusModificationTime = status.getStatusModificationTime();
    if (statusModificationTime != null) {
      dto.setStatusModificationTime(statusModificationTime);
    }
    Integer parityInodeId = status.getParityInodeId();
    if (parityInodeId != null) {
      dto.setParityInodeId(parityInodeId);
    }
    Integer parityStatus = status.getParityStatus();
    if (parityStatus != null) {
      dto.setParityStatus(parityStatus);
    }
    Long parityStatusModificationTime = status.getParityStatusModificationTime();
    if (parityStatusModificationTime != null) {
      dto.setParityStatusModificationTime(parityStatusModificationTime);
    }
    String parityFileName = status.getParityFileName();
    if (parityFileName != null) {
      dto.setParityFileName(parityFileName);
    }
    Integer lostBlocks = status.getLostBlocks();
    if (lostBlocks != null) {
      dto.setLostBlockCount(lostBlocks);
    }
    Integer lostParityBlocks = status.getLostParityBlocks();
    if (lostParityBlocks != null) {
      dto.setLostParityBlockCount(lostParityBlocks);
    }
    Boolean revoked = status.getRevoked();
    if (revoked != null) {
      dto.setRevoked(revoked);
    }
  }

  @Override
  public HopEncodingStatus findByInodeId(int inodeId) throws StorageException {
    HopsSession session = clusterjConnector.obtainSession();
    EncodingStatusDto dto = session.find(EncodingStatusDto.class, inodeId);
    if (dto == null) {
      return null;
    }
    return createHopEncoding(dto);
  }

  @Override
  public HopEncodingStatus findByParityInodeId(int inodeId) throws StorageException {
    HopsSession session = clusterjConnector.obtainSession();
    HopsQueryBuilder builder = session.getQueryBuilder();
    HopsQueryDomainType<EncodingStatusDto> domain = builder
        .createQueryDefinition(EncodingStatusDto.class);
    domain.where(domain.get("parityInodeId").equal(domain.param(PARITY_INODE_ID)));
    HopsQuery<EncodingStatusDto> query = session.createQuery(domain);
    query.setParameter(PARITY_INODE_ID, inodeId);

    List<EncodingStatusDto> results = query.getResultList();
    assert results.size() <= 1;

    if (results.size() == 0) {
      return null;
    }

    return createHopEncoding(results.get(0));
  }

  @Override
  public Collection<HopEncodingStatus> findRequestedEncodings(long limit) throws StorageException {
    return findWithStatus(HopEncodingStatus.ENCODING_REQUESTED, limit);
  }

  @Override
  public int countRequestedEncodings() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, STATUS + "=" + HopEncodingStatus.ENCODING_REQUESTED);
  }

  @Override
  public Collection<HopEncodingStatus> findRequestedRepairs(long limit) throws StorageException {
    /*
     * Prioritize files with more missing blocks and also prioritize source files over parity files.
     * Finally, prioritize earlier detected failures to prevent starvation.
     */
    final String query = "SELECT " + INODE_ID + ", " + STATUS + ", " + CODEC + ", " + TARGET_REPLICATION
        + ", " + PARITY_STATUS + ", " + STATUS_MODIFICATION_TIME  + ", " + PARITY_STATUS_MODIFICATION_TIME
        + ", " + PARITY_INODE_ID + ", " + PARITY_FILE_NAME + ", " + LOST_BLOCKS  + ", " + LOST_PARITY_BLOCKS
        + ", " + LOST_BLOCKS  + "+" + LOST_PARITY_BLOCKS  + " AS " + LOST_BLOCK_SUM + " FROM " + TABLE_NAME
        + " WHERE " + STATUS  + "=" + HopEncodingStatus.REPAIR_REQUESTED + " ORDER BY " + LOST_BLOCK_SUM
        + " DESC, " + LOST_BLOCKS + " DESC, " + STATUS_MODIFICATION_TIME + " ASC LIMIT " + limit;
    return find(query);
  }

  @Override
  public int countRequestedRepairs() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, STATUS + "=" + HopEncodingStatus.REPAIR_REQUESTED);
  }

  @Override
  public Collection<HopEncodingStatus> findActiveEncodings() throws StorageException {
    return findAllWithStatus(HopEncodingStatus.ENCODING_ACTIVE);
  }

  @Override
  public int countActiveEncodings() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, STATUS + "=" + HopEncodingStatus.ENCODING_ACTIVE);
  }

  @Override
  public Collection<HopEncodingStatus> findEncoded(long limit) throws StorageException {
    return findWithStatus(HopEncodingStatus.ENCODED, limit);
  }

  @Override
  public int countEncoded() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, STATUS + "=" + HopEncodingStatus.ENCODED);
  }

  @Override
  public Collection<HopEncodingStatus> findActiveRepairs() throws StorageException {
    return findAllWithStatus(HopEncodingStatus.REPAIR_ACTIVE);
  }

  @Override
  public int countActiveRepairs() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, STATUS + "=" + HopEncodingStatus.REPAIR_ACTIVE);
  }

  @Override
  public Collection<HopEncodingStatus> findRequestedParityRepairs(long limit) throws StorageException {
    final String queryString = "SELECT * FROM %s WHERE %s=%s AND %s!=%s AND %s!=%s ORDER BY %s ASC LIMIT %s";
    String query = String.format(
        STATUS_QUERY,
        TABLE_NAME,
        PARITY_STATUS,
        HopEncodingStatus.PARITY_REPAIR_REQUESTED,
        STATUS,
        HopEncodingStatus.REPAIR_ACTIVE,
        STATUS,
        HopEncodingStatus.REPAIR_FAILED,
        PARITY_STATUS_MODIFICATION_TIME,
        limit);
    return find(query);
  }

  @Override
  public int countRequestedParityRepairs() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, PARITY_STATUS + "=" + HopEncodingStatus.PARITY_REPAIR_REQUESTED);
  }

  @Override
  public Collection<HopEncodingStatus> findActiveParityRepairs() throws StorageException {
    return findAllWithParityStatus(HopEncodingStatus.PARITY_REPAIR_ACTIVE);
  }

  @Override
  public int countActiveParityRepairs() throws StorageException {
    return CountHelper.countWhere(TABLE_NAME, PARITY_STATUS + "=" + HopEncodingStatus.PARITY_REPAIR_ACTIVE);
  }

  @Override
  public void setLostBlockCount(int n) {

  }

  @Override
  public int getLostBlockCount() {
    return 0;
  }

  @Override
  public void setLostParityBlockCount(int n) {

  }

  @Override
  public int getLostParityBlockCount() {
    return 0;
  }

  @Override
  public Collection<HopEncodingStatus> findDeleted(long limit) throws StorageException {
    return findWithStatus(HopEncodingStatus.DELETED, limit);
  }

  @Override
  public Collection<HopEncodingStatus> findRevoked() throws StorageException {
    HopsSession session = clusterjConnector.obtainSession();
    HopsQueryBuilder builder = session.getQueryBuilder();
    HopsQueryDomainType<EncodingStatusDto> domain = builder
        .createQueryDefinition(EncodingStatusDto.class);
    domain.where(domain.get("revoked").equal(domain.param(REVOKED)));
    HopsQuery<EncodingStatusDto> query = session.createQuery(domain);
    query.setParameter(REVOKED, true);

    List<EncodingStatusDto> results = query.getResultList();
    return createHopEncodings(results);
  }

  private List<HopEncodingStatus> createHopEncodings(List<EncodingStatusDto> list) {
    List<HopEncodingStatus> result = new ArrayList<HopEncodingStatus>(list.size());
    for (EncodingStatusDto dto : list) {
      result.add(createHopEncoding(dto));
    }
    return result;
  }

  private HopEncodingStatus createHopEncoding(EncodingStatusDto dto) {
    if (dto == null) {
      return null;
    }

    // This is necessary because Integer cannot be used for clusterj fields with an index.
    // But it shouldn't be 0 anyway as it is always the root folder
    Integer parityInodeId = null;
    if (dto.getParityInodeId() != 0) {
      parityInodeId = dto.getParityInodeId();
    }

    return new HopEncodingStatus(dto.getInodeId(), parityInodeId, dto.getStatus(), dto.getCodec(),
        dto.getTargetReplication(), dto.getStatusModificationTime(), dto.getParityStatus(),
        dto.getParityStatusModificationTime(), dto.getParityFileName(), dto.getLostBlockCount(),
        dto.getLostParityBlockCount(), dto.getRevoked());
  }

  private List<HopEncodingStatus> findAllWithStatus(int status) throws StorageException {
    return findWithStatus(status, Long.MAX_VALUE);
  }

  private List<HopEncodingStatus> findAllWithParityStatus(int status) throws StorageException {
    return findWithParityStatus(status, Long.MAX_VALUE);
  }

  private static final String STATUS_QUERY = "SELECT * FROM %s WHERE %s=%s ORDER BY %s ASC LIMIT %s";

  private List<HopEncodingStatus> findWithParityStatus(int findStatus, long limit) throws StorageException {
    String query = String.format(STATUS_QUERY, TABLE_NAME, PARITY_STATUS, findStatus, PARITY_STATUS_MODIFICATION_TIME,
        limit);
    return find(query);
  }

  private List<HopEncodingStatus> findWithStatus(int findStatus, long limit) throws StorageException {
    String query = String.format(STATUS_QUERY, TABLE_NAME, STATUS, findStatus, STATUS_MODIFICATION_TIME, limit);
    return find(query);
  }

  private List<HopEncodingStatus> find(String query) throws StorageException {
    ArrayList<HopEncodingStatus> resultList;
    try {
      Connection conn = mysqlConnector.obtainSession();
      PreparedStatement s = conn.prepareStatement(query);
      ResultSet result = s.executeQuery();

      resultList = new ArrayList<HopEncodingStatus>();

      while (result.next()) {
        Integer inodeId = result.getInt(INODE_ID);
        Integer parityInodeId = result.getInt(PARITY_INODE_ID);
        Integer status = result.getInt(STATUS);
        String codec = result.getString(CODEC);
        Short targetReplication = result.getShort(TARGET_REPLICATION);
        Long statusModificationTime = result.getLong(STATUS_MODIFICATION_TIME);
        Integer parityStatus = result.getInt(PARITY_STATUS);
        Long parityStatusModificationTime = result.getLong(PARITY_STATUS_MODIFICATION_TIME);
        String parityFileName = result.getString(PARITY_FILE_NAME);
        int lostBlocks = result.getInt(LOST_BLOCKS);
        int lostParityBlocks = result.getInt(LOST_PARITY_BLOCKS);
        // FIXME STEFFEN - This leads to crashed with column revoked not found.
        // No idea why but the revoked flag is not read in this context anyway.
//        Boolean revoked = result.getBoolean(REVOKED);
        resultList.add(new HopEncodingStatus(inodeId, parityInodeId, status, codec, targetReplication,
            statusModificationTime, parityStatus, parityStatusModificationTime, parityFileName, lostBlocks,
            lostParityBlocks, /*revoked*/ null));
      }
    } catch (SQLException ex) {
      throw HopsSQLExceptionHelper.wrap(ex);
    } finally {
      mysqlConnector.closeSession();
    }
    return resultList;
  }
}
