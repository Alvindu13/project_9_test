package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.BusinessProxyImpl;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    @Mock
    private DaoProxy daoProxy;
    @Mock
    private ComptabiliteDao comptabiliteDao;
    /**
     * Test des règles de gestion fonctionnelles de 1 à 5
     * @throws Exception
     */
    @Test
    public void checkEcritureComptableUnitRespecteToutesLesReglesGestions() throws Exception {

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        new BigDecimal(123),
                        null
                )
        );

        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),
                        null,
                        null,
                        new BigDecimal(123)
                )
        );


        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableNonEquilibree() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        new BigDecimal(123),
                        null
                )
        );

        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),
                        null,
                        null,
                        new BigDecimal(350)
                )
        );


        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    @Test
    public void addReferenceSansReferenceEnBaseTest() throws Exception {

        when(comptabiliteDao.getListSequenceEcritureComptable()).thenReturn(Collections.emptyList());
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        new BigDecimal(123),
                        null
                )
        );

        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),
                        null,
                        null,
                        new BigDecimal(350)
                )
        );


        //manager.checkEcritureComptableUnit(vEcritureComptable);

        manager.addReference(vEcritureComptable);

        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable("1");


    }


    @Test
    public void addReferenceAvecReferenceEnBaseTest() throws Exception {

        when(comptabiliteDao.getListSequenceEcritureComptable()).thenReturn(Collections.emptyList());
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        new BigDecimal(123),
                        null
                )
        );

        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(2),
                        null,
                        null,
                        new BigDecimal(350)
                )
        );


        //manager.checkEcritureComptableUnit(vEcritureComptable);

        manager.addReference(vEcritureComptable);

        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable("1");


    }



    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));

        //Il y a une erreur ICI val = 1234 pourtant le test passe (donc ne fonctionne pas)
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

}
