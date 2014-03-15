package se.sics.hop.metadata.ndb.dalimpl.yarn;

import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import com.mysql.clusterj.query.Predicate;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import java.util.Collection;
import java.util.List;
import se.sics.hop.exception.StorageException;
import se.sics.hop.metadata.hdfs.entity.yarn.HopNodeId;
import se.sics.hop.metadata.ndb.ClusterjConnector;
import se.sics.hop.metadata.yarn.dal.NodeIdDataAccess;
import se.sics.hop.metadata.yarn.tabledef.NodeIdTableDef;

/**
 *
 * @author Theofilos Kakantousis <tkak@sics.se>
 */
public class NodeIdClusterJ implements NodeIdTableDef, NodeIdDataAccess<HopNodeId> {

    @PersistenceCapable(table = TABLE_NAME)
    public interface NodeIdDTO {

        @Column(name = ID)
        int getId();

        void setId(int id);

        @PrimaryKey
        @Column(name = HOST)
        String getHost();

        void setHost(String host);

        @PrimaryKey
        @Column(name = PORT)
        int getPort();

        void setPort(int port);
    }
    private ClusterjConnector connector = ClusterjConnector.getInstance();

    @Override
    public HopNodeId findById(int id) throws StorageException {
        Session session = connector.obtainSession();

        /*NodeIdDTO nodeidDTO = null;
         if (session != null) {
         nodeidDTO = session.find(NodeIdDTO.class, id);
         }
         if (nodeidDTO == null) {
         throw new StorageException("HOP :: Error while retrieving row");
         }
         * */
        QueryBuilder qb = session.getQueryBuilder();

        QueryDomainType<NodeIdDTO> dobj = qb.createQueryDefinition(NodeIdDTO.class);
        Predicate pred1 = dobj.get("id").equal(dobj.param("id"));
        dobj.where(pred1);
        Query<NodeIdDTO> query = session.createQuery(dobj);
        query.setParameter("id", id);

        List<NodeIdDTO> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return createHopNodeId(results.get(0));
        } else {
            return null;
        }

    }

    @Override
    public HopNodeId findByHostPort(String host, int port) throws StorageException {
        Session session = connector.obtainSession();
        Object[] objarr = new Object[2];
        objarr[0] = host;
        objarr[1] = port;
        NodeIdDTO nodeidDTO = null;
        if (session != null) {
            nodeidDTO = session.find(NodeIdDTO.class, objarr);
        }
        if (nodeidDTO == null) {
            throw new StorageException("HOP :: Error while retrieving row");
        }

        return createHopNodeId(nodeidDTO);
    }

    @Override
    public void prepare(Collection<HopNodeId> modified, Collection<HopNodeId> removed) throws StorageException {
        Session session = connector.obtainSession();
        try {
            if (removed != null) {
                for (HopNodeId nodeid : removed) {
                    Object[] objarr = new Object[2];
                    objarr[0] = nodeid.getHost();
                    objarr[1] = nodeid.getPort();
                    NodeIdDTO persistable = session.newInstance(NodeIdDTO.class, objarr);
                    session.deletePersistent(persistable);
                }
            }
            if (modified != null) {
                for (HopNodeId nodeid : modified) {
                    NodeIdDTO persistable = createPersistable(nodeid, session);
                    session.savePersistent(persistable);
                }
            }
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createNodeId(HopNodeId nodeId) throws StorageException {
        Session session = connector.obtainSession();
        createPersistable(nodeId, session);
    }

    private NodeIdDTO createPersistable(HopNodeId hopNodeId, Session session) {
        NodeIdDTO nodeDTO = session.newInstance(NodeIdDTO.class);
        System.out.println("NodeIdClusterJ :: creating NodeID with id:" + hopNodeId.getId());
        //Set values to persist new rmnode
        nodeDTO.setId(hopNodeId.getId());
        nodeDTO.setHost(hopNodeId.getHost());
        nodeDTO.setPort(hopNodeId.getPort());
        session.savePersistent(nodeDTO);
        return nodeDTO;
    }

    /**
     * Transforms a DTO to Hop object.
     *
     * @param rmDTO
     * @return HopRMNode
     */
    private HopNodeId createHopNodeId(NodeIdDTO nodeidDTO) {
        return new HopNodeId(nodeidDTO.getId(), nodeidDTO.getHost(), nodeidDTO.getPort());
    }
}
