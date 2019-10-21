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
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
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
    public void test_Ecriture_Comptablet_Respecte_Toutes_Les_Regles_Gestions_Until_5() throws Exception {

        //  Given
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));

        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));

            // On set la référence
        vEcritureComptable.setReference("AC-2019/00001");

            // On set les dépendances et les valeurs des méthodes appelés
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getSequenceEcritureComptableByYear(2019)).thenReturn(new SequenceEcritureComptable(2019, 1));
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));

        //  When & Then
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }




    @Test
    public void test_add_Reference_Sans_Reference_En_Base() throws Exception {

        when(comptabiliteDao.getListSequenceEcritureComptable()).thenReturn(Collections.emptyList());
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date1 = "22/06/2019";
        date = simpleDateFormat.parse(date1);

        vEcritureComptable.setDate(date);
        Integer pAnneeEcriture = 2019;
        manager.addReference(vEcritureComptable);
        SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable(pAnneeEcriture, 1);
        verify(comptabiliteDao, times(1)).insertSequenceEcritureComptable(eq(vSequenceEcritureComptable));
        assertEquals(vEcritureComptable.getReference(), "AC-2019/00001");

    }


    @Test
    public void test_add_Reference_Avec_Reference_En_Base_Test() throws Exception {

        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setJournal(
                new JournalComptable(
                        "AC",
                        "Achat")
        );

        Integer pAnneeEcriture = 2019;

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


        assertEquals(vEcritureComptable.getReference(), "AC-2019/00013");

        //assertEquals("captureSequenceEcritureComptable.get(0).toString(1)", vSequenceEcritureComptable);
        //verify(comptabiliteDao, times(1)).updateSequenceEcritureComptable(refEq(vSequenceEcritureComptable));
    }


    @Test
    public void test_check_Ecriture_Comptable_Context() throws Exception {
        //  Given
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));

        // On set les dépendances et les valeurs des méthodes appelés
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        when(comptabiliteDao.getEcritureComptableByRef("AC-2019/00001")).thenReturn(vEcritureComptable);

        //  When & Then
        manager.checkEcritureComptableContext(vEcritureComptable);
    }



    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_Violation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(1234)));

        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG3() throws Exception {
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


    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG3_Sans_Ligne_Ecriture_En_Debit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        null,
                        new BigDecimal(123)
                )
        );
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    // TODO COMMENT SAVOIR SI L'exeption a bien été appelée pour cette erreur ?
    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG3_Sans_Ligne_Ecriture_En_Credit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        new BigDecimal(123),
                        null
                )
        );
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_RG3_Avec_2_Lignes_En_Credit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(
                        new CompteComptable(1),
                        null,
                        null,
                        new BigDecimal(350)
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


    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_RG3_Avec_2_Lignes_En_Debit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setDate(new Date());
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
                        new BigDecimal(350),
                        null
                )
        );
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG5_Annee_Ref_Differente_Annee_Ecriture() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.setReference("AC-2018/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Unit_RG5_Code_Journal_Different() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null,null,
                new BigDecimal(123)));
        vEcritureComptable.setReference("AB-2019/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Context_RG6_ID_DIFFERENT() throws Exception {
        //  Given

            // actual
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));

            // expected
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setId(2);
        pEcritureComptable.setJournal(vEcritureComptable.getJournal());
        pEcritureComptable.setDate(vEcritureComptable.getDate());
        pEcritureComptable.setReference(vEcritureComptable.getReference());
        pEcritureComptable.setLibelle(vEcritureComptable.getLibelle());
        pEcritureComptable.getListLigneEcriture().add(vEcritureComptable.getListLigneEcriture().get(0));
        pEcritureComptable.getListLigneEcriture().add(vEcritureComptable.getListLigneEcriture().get(1));

        // On set les dépendances et les valeurs des méthodes appelés
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);

        when(comptabiliteDao.getEcritureComptableByRef("AC-2019/00001")).thenReturn(pEcritureComptable);

        //  When & Then
        manager.checkEcritureComptableContext(vEcritureComptable);
    }


    @Test(expected = FunctionalException.class)
    public void test_check_Ecriture_Comptable_Context_RG6_ID_NULL() throws Exception {
        //  Given

        // actual
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));

        // expected
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setId(null);
        pEcritureComptable.setJournal(vEcritureComptable.getJournal());
        pEcritureComptable.setDate(vEcritureComptable.getDate());
        pEcritureComptable.setReference(vEcritureComptable.getReference());
        pEcritureComptable.setLibelle(vEcritureComptable.getLibelle());
        pEcritureComptable.getListLigneEcriture().add(vEcritureComptable.getListLigneEcriture().get(0));
        pEcritureComptable.getListLigneEcriture().add(vEcritureComptable.getListLigneEcriture().get(1));

        // On set les dépendances et les valeurs des méthodes appelés
        ComptabiliteManagerImpl.configure(mock(BusinessProxy.class), daoProxy, mock(TransactionManager.class));
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);

        when(comptabiliteDao.getEcritureComptableByRef("AC-2019/00001")).thenReturn(pEcritureComptable);

        //  When & Then
        manager.checkEcritureComptableContext(vEcritureComptable);
    }



    /*@Test
    public void checkSequenceEcritureComptableViolationAnneeDoublonTest(){



    }

    @Test
    public void checkSequenceEcritureComptableViolationDerniereValeurDoublonTest(){

    }*/

}
