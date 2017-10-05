/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skl.skltpservices.adapter.fk.util;

import se.inera.ifv.insuranceprocess.healthreporting.medcertqa.v1.LakarutlatandeEnkelType;
import se.inera.ifv.insuranceprocess.healthreporting.medcertqa.v1.VardAdresseringsType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.EnhetType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.HosPersonalType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.PatientType;
import se.inera.ifv.insuranceprocess.healthreporting.v2.VardgivareType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public final class ValidatorUtil {
    private static final String PERSON_NUMBER_REGEX = "[0-9]{8}[-+][0-9]{4}";
    private static final String PERSON_NUMBER_WITHOUT_DASH_REGEX = "[0-9]{12}";

	public static String getValidationErrors(ArrayList<String> validationErrors) {
		int i = 1;
		StringBuffer validationString = new StringBuffer();
		Iterator<String> iterValidationErrors = validationErrors.iterator();
		validationString.append("Validation error " + i++ + ":");
		validationString.append((String) iterValidationErrors.next());
		while (iterValidationErrors.hasNext()) {
			validationString.append("\n\rValidation error " + i++ + ":");
			validationString.append((String) iterValidationErrors.next());
		}
		return validationString.toString();
	}

	public static void validatePatient(ArrayList<String> validationErrors, PatientType inPatient) throws Exception {
        if (inPatient == null) {
            validationErrors.add("No Patient element found!");
            throw new Exception();
        }
        // Check patient id - mandatory
        if (inPatient.getPersonId() == null || inPatient.getPersonId().getExtension() == null
                || inPatient.getPersonId().getExtension().length() < 1) {
            validationErrors.add("No Patient Id found!");
        }
        // Check patient o.i.d.
        if (inPatient.getPersonId() == null
                || inPatient.getPersonId().getRoot() == null
                || (!inPatient.getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.1") && !inPatient
                        .getPersonId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.3.3"))) {
            validationErrors
                    .add("Wrong o.i.d. for Patient Id! Should be 1.2.752.129.2.1.3.1 or 1.2.752.129.2.1.3.3");
        }

        String inPersonnummer = inPatient.getPersonId().getExtension();

        //Correct personnummer without dash
        if (inPersonnummer.length() == 12 && Pattern.matches(PERSON_NUMBER_WITHOUT_DASH_REGEX, inPersonnummer)) {
            inPersonnummer = inPersonnummer.substring(0,8) + "-" + inPersonnummer.substring(8);
            inPatient.getPersonId().setExtension(inPersonnummer);
        }

        // Check format of patient id (has to be a valid personnummer)
        if (inPersonnummer == null || !Pattern.matches(PERSON_NUMBER_REGEX, inPersonnummer)) {
            validationErrors.add("Wrong format for person-id! Valid format is YYYYMMDD-XXXX or YYYYMMDD+XXXX.");
        }

    }

    public static void validateHoSPersonal(ArrayList<String> validationErrors, HosPersonalType inHoSP) throws Exception {
        // Check lakar id - mandatory
        if (inHoSP.getPersonalId() == null || inHoSP.getPersonalId().getExtension() == null
                || inHoSP.getPersonalId().getExtension().length() < 1) {
            validationErrors.add("No personal-id found!");
        }
        // Check lakar id o.i.d.
        if (inHoSP.getPersonalId() == null || inHoSP.getPersonalId().getRoot() == null
                || !inHoSP.getPersonalId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
            validationErrors.add("Wrong o.i.d. for personalId! Should be 1.2.752.129.2.1.4.1");
        }

        // Check lakarnamn - mandatory
        if (inHoSP.getFullstandigtNamn() == null || inHoSP.getFullstandigtNamn().length() < 1) {
            validationErrors.add("No skapadAvHosPersonal fullstandigtNamn elements found or set!");
        }

        // Check that we got a enhet element
        if (inHoSP.getEnhet() == null) {
            validationErrors.add("No enhet element found!");
            throw new Exception();
        }
        EnhetType inEnhet = inHoSP.getEnhet();

        // Check enhets id - mandatory
        if (inEnhet.getEnhetsId() == null || inEnhet.getEnhetsId().getExtension() == null
                || inEnhet.getEnhetsId().getExtension().length() < 1) {
            validationErrors.add("No enhets-id found!");
        }
        // Check enhets o.i.d
        if (inEnhet.getEnhetsId() == null || inEnhet.getEnhetsId().getRoot() == null
                || !inEnhet.getEnhetsId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
            validationErrors.add("Wrong o.i.d. for enhetsId! Should be 1.2.752.129.2.1.4.1");
        }

        // Check enhetsnamn - mandatory
        if (inEnhet.getEnhetsnamn() == null || inEnhet.getEnhetsnamn().length() < 1) {
            validationErrors.add("No enhetsnamn found!");
        }

        // Check that we got a vardgivare element
        if (inEnhet.getVardgivare() == null) {
            validationErrors.add("No vardgivare element found!");
            throw new Exception();
        }
        VardgivareType inVardgivare = inEnhet.getVardgivare();

        // Check vardgivare id - mandatory
        if (inVardgivare.getVardgivareId() == null || inVardgivare.getVardgivareId().getExtension() == null
                || inVardgivare.getVardgivareId().getExtension().length() < 1) {
            validationErrors.add("No vardgivare-id found!");
        }
        // Check vardgivare o.i.d.
        if (inVardgivare.getVardgivareId() == null || inVardgivare.getVardgivareId().getRoot() == null
                || !inVardgivare.getVardgivareId().getRoot().equalsIgnoreCase("1.2.752.129.2.1.4.1")) {
            validationErrors.add("Wrong o.i.d. for vardgivareId! Should be 1.2.752.129.2.1.4.1");
        }

        // Check vardgivarename - mandatory
        if (inVardgivare.getVardgivarnamn() == null || inVardgivare.getVardgivarnamn().length() < 1) {
            validationErrors.add("No vardgivarenamn found!");
        }
    }

    public static void validateLakarutlatande(ArrayList<String> validationErrors, LakarutlatandeEnkelType inLakarUtlatande) throws Exception {
        // Läkarutlåtande referens - mandatory
        if (inLakarUtlatande == null) {
            validationErrors.add("No lakarutlatande element found!");
            throw new Exception();
        }
        // Läkarutlåtande referens - id - mandatory
        if (inLakarUtlatande.getLakarutlatandeId() == null || inLakarUtlatande.getLakarutlatandeId().length() < 1) {
            validationErrors.add("No lakarutlatande-id found!");
        }

        // Läkarutlåtande referens - signeringsTidpunkt - mandatory
        if (inLakarUtlatande.getSigneringsTidpunkt() == null) {
            validationErrors.add("No or wrong lakarutlatande-signeringsTidpunkt found!");
        }

        //Check patient information
        PatientType inPatient = inLakarUtlatande.getPatient();
        validatePatient(validationErrors, inPatient);
    }

	public static void validateAdressVard(ArrayList<String> validationErrors, VardAdresseringsType inAddressVard) throws Exception {
        if (inAddressVard == null) {
            validationErrors.add("No adressVard element found!");
            throw new Exception();
        }
        if (inAddressVard.getHosPersonal() == null) {
            validationErrors.add("No adressVard - hosPersonal element found!");
            throw new Exception();
        }
        HosPersonalType inHoSP = inAddressVard.getHosPersonal();
        validateHoSPersonal(validationErrors, inHoSP);
    }

}
