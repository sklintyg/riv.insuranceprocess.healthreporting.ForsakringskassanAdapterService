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
package se.skl.skltpservices.adapter.fk.sendmedcertquestion;

import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.getValidationErrors;
import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.validateAdressVard;
import static se.skl.skltpservices.adapter.fk.util.ValidatorUtil.validateLakarutlatande;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.ifv.insuranceprocess.healthreporting.medcertqa.v1.Amnetyp;
import se.inera.ifv.insuranceprocess.healthreporting.medcertqa.v1.LakarutlatandeEnkelType;
import se.inera.ifv.insuranceprocess.healthreporting.medcertqa.v1.VardAdresseringsType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.QuestionToFkType;
import se.inera.ifv.insuranceprocess.healthreporting.sendmedicalcertificatequestionresponder.v1.SendMedicalCertificateQuestionType;

public class VardRequest2FkValidator {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void validateRequest(SendMedicalCertificateQuestionType parameters) throws Exception {
		// List of validation errors
		ArrayList<String> validationErrors = new ArrayList<String>();
		// Validate incoming request
		try {
			// Check that we got any data at all
			if (parameters == null) {
				validationErrors.add("No SendMedicalCertificateQuestion found in incoming data!");
				throw new Exception();
			}

			// Check that we got an question element
			if (parameters.getQuestion() == null) {
				validationErrors.add("No Question element found in incoming request data!");
				throw new Exception();
			}

			QuestionToFkType inQuestion = parameters.getQuestion();

			/**
			 * Check meddelande data + lakarutlatande reference
			 */

			// Meddelande id - mandatory
			if (inQuestion.getVardReferensId() == null || inQuestion.getVardReferensId().length() < 1) {
				validationErrors.add("No vardReferens-id found!");
			}

			// Ämne - mandatory
			Amnetyp inAmne = inQuestion.getAmne();
			if (inAmne == null) {
				validationErrors.add("No Amne element found!");
			}

			/**
			 * Check that we got a question
			 */
			if (inQuestion.getFraga() == null) {
				validationErrors.add("No Question fraga element found!");
				throw new Exception();
			}
			if (inQuestion.getFraga().getMeddelandeText() == null
					|| inQuestion.getFraga().getMeddelandeText().length() < 1) {
				validationErrors.add("No Question fraga meddelandeText elements found or set!");
			}
			if (inQuestion.getFraga().getSigneringsTidpunkt() == null) {
				validationErrors.add("No Question fraga signeringsTidpunkt elements found or set!");
			}

			// Avsänt tidpunkt - mandatory
			if (inQuestion.getAvsantTidpunkt() == null) {
				validationErrors.add("No or wrong avsantTidpunkt found!");
			}

			LakarutlatandeEnkelType inLakarUtlatande = inQuestion.getLakarutlatande();
			validateLakarutlatande(validationErrors, inLakarUtlatande);

			/**
			 * Check avsändar data.
			 */
			VardAdresseringsType inAddressVard = inQuestion.getAdressVard();
			validateAdressVard(validationErrors, inAddressVard);

			// Check if we got any validation errors that didn't cause an
			// Exception
			if (validationErrors.size() > 0) {
				logger.error("Validate exception:" + getValidationErrors(validationErrors));
				throw new Exception();
			}

			// No validation errors!

		} catch (Exception e) {
			throw new Exception(getValidationErrors(validationErrors));
		}
	}

}
