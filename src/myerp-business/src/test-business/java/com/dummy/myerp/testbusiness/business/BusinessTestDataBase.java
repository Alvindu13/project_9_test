package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;



public class BusinessTestDataBase extends BusinessTestCase {



    @Autowired
    private DataSource dataSource;



    /*@Test
    public void should_save_ecriture_comptable() throws FunctionalException {

        // given
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setLibelle("testTT");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(123)));


        // when
        SpringRegistry.getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptable);

        // then
        /*Long petNumber = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM ecriture_comptable WHERE reference = ?", Long.class, 1, "BQ-2019/00001");
        Assert.assertEquals(java.util.Optional.ofNullable(petNumber), 1L);
    }*/


    @Test
    public void should_save_sequence_ecriture_comptable() throws FunctionalException {

        // given
        SequenceEcritureComptable vSequenceEcritureComptable =
                new SequenceEcritureComptable("AC", 2019, 1);


        // when
        SpringRegistry.getBusinessProxy().getComptabiliteManager().insertSequenceEcritureComptable(vSequenceEcritureComptable);

        // then
        /*Long petNumber = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(*) FROM ecriture_comptable WHERE reference = ?", Long.class, 1, "BQ-2019/00001");
        Assert.assertEquals(java.util.Optional.ofNullable(petNumber), 1L);*/
    }



}
