package se.sics.hop.metadata.ndb.dalimpl.yarn;

import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.hop.exception.StorageException;
import se.sics.hop.metadata.hdfs.entity.yarn.HopFiCaSchedulerApp;
import se.sics.hop.metadata.ndb.ClusterjConnector;
import se.sics.hop.metadata.yarn.dal.FiCaSchedulerAppDataAccess;
import se.sics.hop.metadata.yarn.tabledef.FiCaSchedulerAppTableDef;
import static se.sics.hop.metadata.yarn.tabledef.FiCaSchedulerAppTableDef.RESOURCELIMIT_ID;

/**
 *
 * @author nickstanogias
 */
public class FiCaSchedulerAppClusterJ implements FiCaSchedulerAppTableDef, FiCaSchedulerAppDataAccess<HopFiCaSchedulerApp> {


    @PersistenceCapable(table = TABLE_NAME)
    public interface FiCaSchedulerAppDTO {

        @PrimaryKey
        @Column(name = APPID)
        int getAppId();

        void setAppId(int appId);
        
        @Column(name = ATTEMPTID)
        int getAttemptid();

        void setAttemptid(int appattemptid);

        @Column(name = CURRENTRESERVATION_ID)
        int getcurrentreservationid();

        void setcurrentreservationid(int currentreservationid);

        @Column(name = RESOURCELIMIT_ID)
        int getresourcelimitid();

        void setresourcelimitid(int resourcelimitid);

        @Column(name = CURRENTCONSUMPTION_ID)
        int getcurrentconsumptionid();

        void setcurrentconsumptionid(int currentconsumptionid);

        @Column(name = ISSTOPPED)
        boolean getIsstopped();

        void setIsstopped(boolean isstopped);
    }
    private final ClusterjConnector connector = ClusterjConnector.getInstance();

    @Override
    public HopFiCaSchedulerApp findById(int id) throws StorageException {
        Session session = connector.obtainSession();

        FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO fiCaSchedulerAppDTO = null;
        if (session != null) {
            fiCaSchedulerAppDTO = session.find(FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO.class, id);
        }
        if (fiCaSchedulerAppDTO == null) {
            throw new StorageException("HOP :: Error while retrieving row");
        }

        return createHopFiCaSchedulerApp(fiCaSchedulerAppDTO);
    }
    
    @Override
    public List<HopFiCaSchedulerApp> findAll() throws StorageException {
        Session session = connector.obtainSession();
        QueryBuilder qb = session.getQueryBuilder();
        QueryDomainType<FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO> dobj = qb.createQueryDefinition(FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO.class);
        Query<FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO> query = session.createQuery(dobj);
        List<FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO> results = query.getResultList();
        try {
            return createFiCaSchedulerAppList(results);
        } catch (IOException ex) {
            Logger.getLogger(FiCaSchedulerAppClusterJ.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<HopFiCaSchedulerApp> createFiCaSchedulerAppList(List<FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO> list) throws IOException {
        List<HopFiCaSchedulerApp> fiCaSchedulerApps = new ArrayList<HopFiCaSchedulerApp>();
        for (FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO persistable : list) {
            fiCaSchedulerApps.add(createHopFiCaSchedulerApp(persistable));
        }
        return fiCaSchedulerApps;
    }

    @Override
    public void prepare(Collection<HopFiCaSchedulerApp> modified, Collection<HopFiCaSchedulerApp> removed) throws StorageException {
        Session session = connector.obtainSession();
        try {
            if (removed != null) {
                for (HopFiCaSchedulerApp hop : removed) {
                    FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO persistable = 
                            session.newInstance(FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO.class, 
                                    hop.getAppId());
                    session.deletePersistent(persistable);
                }
            }
            if (modified != null) {
                for (HopFiCaSchedulerApp hop : modified) {
                    FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO persistable = createPersistable(hop, session);
                    session.savePersistent(persistable);
                }
            }
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    private HopFiCaSchedulerApp createHopFiCaSchedulerApp(FiCaSchedulerAppDTO fiCaSchedulerAppDTO) {
        return new HopFiCaSchedulerApp(fiCaSchedulerAppDTO.getAppId(),
                fiCaSchedulerAppDTO.getAttemptid(),
                fiCaSchedulerAppDTO.getcurrentreservationid(),
                fiCaSchedulerAppDTO.getresourcelimitid(),
                fiCaSchedulerAppDTO.getcurrentconsumptionid(),
                fiCaSchedulerAppDTO.getIsstopped());
    }

    private FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO createPersistable(HopFiCaSchedulerApp hop, Session session) {
        FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO fiCaSchedulerAppDTO = session.newInstance(FiCaSchedulerAppClusterJ.FiCaSchedulerAppDTO.class);

        fiCaSchedulerAppDTO.setAppId(hop.getAppId());
        fiCaSchedulerAppDTO.setAttemptid(hop.getAttemptId());
        fiCaSchedulerAppDTO.setcurrentreservationid(hop.getCurrentreservation_id());
        fiCaSchedulerAppDTO.setresourcelimitid(hop.getResourcelimit_id());
        fiCaSchedulerAppDTO.setcurrentconsumptionid(hop.getCurrentconsumption_id());
        fiCaSchedulerAppDTO.setIsstopped(hop.isIsstoped());

        return fiCaSchedulerAppDTO;
    }
}
