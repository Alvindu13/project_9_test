package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class BusinessTestDataBase extends BusinessTestCase {


    @Test
    public void testDatabase() throws Exception {
        Connection connection = SpringRegistry.getDatasource().getConnection();
        connection.prepareStatement("INSERT INTO myerp.compte_comptable values (999, 'compte_testing')").execute();

        assertTrue(
                connection.prepareStatement("SELECT * from myerp.compte_comptable where numero = 999").executeQuery().next());
    }

    @Test
    public void should_create_ecriture_comptable() throws FunctionalException {
        // given
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("testTT");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411), null, null, new BigDecimal(123)));

        // when
        SpringRegistry.getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptable);

        // then
        Long writeNumber = new JdbcTemplate(SpringRegistry.getDatasource())
                .queryForObject("SELECT COUNT(*) FROM myerp.ecriture_comptable WHERE reference = ?", Long.class, "AC-2019/00001");
        assertEquals(writeNumber, Long.valueOf(1L));
    }

    @Test
    public void should_read_ecriture_comptable(){

        List<EcritureComptable> list = SpringRegistry.getBusinessProxy().getComptabiliteManager().getListEcritureComptable();

        // then
        Assert.assertNotNull(list);
        Long sizeRead = new JdbcTemplate(SpringRegistry.getDatasource())
                .queryForObject("SELECT COUNT(*) FROM myerp.ecriture_comptable", Long.class);
        assertEquals(sizeRead, Long.valueOf(list.size()));
    }


    @Test
    @Order(1)
    public void should_create_sequence_ecriture_comptable() throws FunctionalException {
        //given
        SequenceEcritureComptable vSequenceEcritureComptable =
                new SequenceEcritureComptable("BQ", 2019, 4);

        // when
        SpringRegistry.getBusinessProxy().getComptabiliteManager().insertSequenceEcritureComptable(vSequenceEcritureComptable);

        // then
        Long writeNumber = new JdbcTemplate(SpringRegistry.getDatasource())
                .queryForObject("SELECT COUNT(*) FROM myerp.sequence_ecriture_comptable WHERE journal_code = ? AND annee = ? AND derniere_Valeur = ?", Long.class, "BQ", 2019, 4);
        assertEquals(writeNumber, Long.valueOf(1L));
    }


       /*@Test
    public void should_update_ecriture_comptable() throws FunctionalException {
        // given
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("newLibelle");
        vEcritureComptable.setReference("AC-2019/00004");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), null, new BigDecimal(223), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411), null, null, new BigDecimal(223)));

        // when
        SpringRegistry.getBusinessProxy().getComptabiliteManager().updateEcritureComptable(vEcritureComptable);

        // then
        Long writeNumber = new JdbcTemplate(SpringRegistry.getDatasource())
                .queryForObject("SELECT COUNT(*) FROM myerp.ecriture_comptable WHERE reference = ?", Long.class, "AC-2019/00001");
        assertEquals(writeNumber, Long.valueOf(1L));
    }*/

    @Test
    public void should_delete_ecriture_comptable() throws FunctionalException {
    }




}
