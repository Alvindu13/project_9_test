package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void isEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        //cas 1 : le compte est équilibré
        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33.00"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301.00"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40.00", "7.00"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());


        //cas 2 : le compte n'est pas équilibré
        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10.00", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20.00", "1.00"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30.00"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1.00", "2.00"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

}
