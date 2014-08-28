/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.sics.hop.metadata.ndb.dalimpl.yarn;

import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import com.mysql.clusterj.query.Predicate;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import se.sics.hop.exception.StorageException;
import se.sics.hop.metadata.hdfs.entity.yarn.HopFiCaSchedulerAppLiveContainers;
import se.sics.hop.metadata.ndb.ClusterjConnector;
import se.sics.hop.metadata.yarn.dal.FiCaSchedulerAppLiveContainersDataAccess;
import se.sics.hop.metadata.yarn.tabledef.FiCaSchedulerAppLiveContainersTableDef;

/**
 *
 * @author Nikos Stanogias <niksta@sics.se>
 */
public class FiCaSchedulerAppLiveContainersClusterJ implements FiCaSchedulerAppLiveContainersTableDef, FiCaSchedulerAppLiveContainersDataAccess<HopFiCaSchedulerAppLiveContainers>{

    @PersistenceCapable(table = TABLE_NAME)
    public interface FiCaSchedulerAppLiveContainersDTO {

        @PrimaryKey
        @Column(name = FICASCHEDULERAPP_ID)
        String getficaschedulerapp_id();
        void setficaschedulerapp_id(String ficaschedulerapp_id);

        @Column(name = CONTAINERID_ID)
        String getcontaineridid();
        void setcontaineridid(String containeridid);
        
        @Column(name = RMCONTAINER_ID)
        String getrmcontainerid();
        void setrmcontainerid(String rmcontainerid);
    }
    private final ClusterjConnector connector = ClusterjConnector.getInstance();
    
    @Override
    public List<HopFiCaSchedulerAppLiveContainers> findById(String ficaId) throws StorageException {
        try {
            Session session = connector.obtainSession();
            QueryBuilder qb = session.getQueryBuilder();

            QueryDomainType<FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO> dobj = qb.createQueryDefinition(FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO.class);
            Predicate pred1 = dobj.get("ficaschedulerapp_id").equal(dobj.param("ficaschedulerapp_id"));
            dobj.where(pred1);
            Query<FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO> query = session.createQuery(dobj);
            query.setParameter("ficaschedulerapp_id", ficaId);

            List<FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO> results = query.getResultList();
            return createFiCaSchedulerAppLiveContainersList(results);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void prepare(Collection<HopFiCaSchedulerAppLiveContainers> modified, Collection<HopFiCaSchedulerAppLiveContainers> removed) throws StorageException {
        Session session = connector.obtainSession();
        try {
            if (removed != null) {
                for (HopFiCaSchedulerAppLiveContainers hop : removed) {
                    FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO persistable = session.newInstance(FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO.class, hop.getFicaschedulerapp_id());
                    session.deletePersistent(persistable);
                }
            }
            if (modified != null) {
                for (HopFiCaSchedulerAppLiveContainers hop : modified) {
                    FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO persistable = createPersistable(hop, session);
                    session.savePersistent(persistable);
                }
            }
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
    
    private HopFiCaSchedulerAppLiveContainers createHopFiCaSchedulerAppLiveContainers(FiCaSchedulerAppLiveContainersDTO fiCaSchedulerAppLiveContainersDTO) {
        return new HopFiCaSchedulerAppLiveContainers(fiCaSchedulerAppLiveContainersDTO.getficaschedulerapp_id(),
                                                    fiCaSchedulerAppLiveContainersDTO.getcontaineridid(),
                                                    fiCaSchedulerAppLiveContainersDTO.getrmcontainerid());
    }

    private FiCaSchedulerAppLiveContainersDTO createPersistable(HopFiCaSchedulerAppLiveContainers hop, Session session) {
        FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO fiCaSchedulerAppLiveContainersDTO = session.newInstance(FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO.class);
        
        fiCaSchedulerAppLiveContainersDTO.setficaschedulerapp_id(hop.getFicaschedulerapp_id());
        fiCaSchedulerAppLiveContainersDTO.setcontaineridid(hop.getContainerid_id());
        fiCaSchedulerAppLiveContainersDTO.setrmcontainerid(hop.getRmcontainer_id());
        
        return fiCaSchedulerAppLiveContainersDTO;
    }
    
    private List<HopFiCaSchedulerAppLiveContainers> createFiCaSchedulerAppLiveContainersList(List<FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO> results) {
        List<HopFiCaSchedulerAppLiveContainers> ficaSchedulerAppLiveContainers = new ArrayList<HopFiCaSchedulerAppLiveContainers>();
        for (FiCaSchedulerAppLiveContainersClusterJ.FiCaSchedulerAppLiveContainersDTO persistable : results) {
            ficaSchedulerAppLiveContainers.add(createHopFiCaSchedulerAppLiveContainers(persistable));
        }
        return ficaSchedulerAppLiveContainers;
    }  
}
