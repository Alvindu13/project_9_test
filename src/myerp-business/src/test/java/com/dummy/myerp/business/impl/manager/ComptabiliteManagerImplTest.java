package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
     *
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

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(vEcritureComptable.getDate());
        Integer pAnneeEcriture = calendar.get(Calendar.YEAR);

        manager.addReference(vEcritureComptable);

        SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable(pAnneeEcriture, 1);

        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable(refEq(vSequenceEcritureComptable));


    }


    @Test
    public void addReferenceAvecReferenceEnBaseTest() throws Exception {

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(vEcritureComptable.getDate());
        Integer pAnneeEcriture = calendar.get(Calendar.YEAR);

        List<SequenceEcritureComptable> sequenceEcritureComptables = new ArrayList<>();
        sequenceEcritureComptables.add(new SequenceEcritureComptable(pAnneeEcriture, 10));
        sequenceEcritureComptables.add(new SequenceEcritureComptable(pAnneeEcriture, 11));
        sequenceEcritureComptables.add(new SequenceEcritureComptable(pAnneeEcriture, 12));

        when(comptabiliteDao
                .getListSequenceEcritureComptable())
                .thenReturn(Collections.synchronizedList(sequenceEcritureComptables));
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));

        manager.addReference(vEcritureComptable);

        SequenceEcritureComptable vSequenceEcritureComptable =
                new SequenceEcritureComptable(
                        pAnneeEcriture,
                        sequenceEcritureComptables
                                .get(sequenceEcritureComptables.size() - 1)
                                .getDerniereValeur() + 1
                );

        // pour les tests ->
        // sequenceEcritureComptables.set(pAnneeEcriture, actual : 13)

        ArgumentCaptor<SequenceEcritureComptable> sequenceEcritureComptableArgumentCaptor =
                ArgumentCaptor.forClass(SequenceEcritureComptable.class);

        verify(comptabiliteDao, times(1))
                .updateSequenceEcritureComptable(sequenceEcritureComptableArgumentCaptor.capture());

        List<SequenceEcritureComptable> captureSequenceEcritureComptable =
                sequenceEcritureComptableArgumentCaptor.getAllValues();

        SequenceEcritureComptable expectedSeqEC = captureSequenceEcritureComptable.get(0);
        SequenceEcritureComptable actualSeqEC = vSequenceEcritureComptable;

        assertThat("Le résultat attendu n'est pas correct. Vérifiez la date d'écriture ou la dernière valeur de la séquence",
                expectedSeqEC, is(actualSeqEC));

        //assertEquals("captureSequenceEcritureComptable.get(0).toString(1)", vSequenceEcritureComptable);
        //verify(comptabiliteDao, times(1)).updateSequenceEcritureComptable(refEq(vSequenceEcritureComptable));
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
