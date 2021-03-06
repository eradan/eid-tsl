/*
 * eID TSL Project.
 * Copyright (C) 2009-2013 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.fedict.eid.tsl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.CollationKey;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;

import be.fedict.eid.tsl.jaxb.tsl.ElectronicAddressType;
import be.fedict.eid.tsl.jaxb.tsl.NonEmptyMultiLangURIType;
import be.fedict.eid.tsl.jaxb.tsl.ObjectFactory;
import be.fedict.eid.tsl.jaxb.tsl.PostalAddressType;


/**
 * Factory for the Belgian Trust Service List.
 * 
 * @author fcorneli
 * 
 */
public class BelgianTrustServiceListFactory {
	
	private final ObjectFactory objectFactory;
	
	private static final Log LOG = LogFactory
			.getLog(BelgianTrustServiceListFactory.class);

	private BelgianTrustServiceListFactory() {
		super();
		this.objectFactory = new ObjectFactory();
	}

	public static enum Trimester {
		FIRST, SECOND, THIRD
	}

	/**
	 * Creates a new instance of a trust service list for Belgium according to
	 * the given time frame.
	 * 
	 * @param year
	 *            the year for which the TSL should be valid.
	 * @param trimester
	 *            the trimester for which the TSL should be valid.
	 * @return the trust service list object.
	 */
	public static TrustServiceList newInstance(int year, Trimester trimester) {
		if (2010 != year && 2011 != year && 2012 != year && 2013 != year && 2014 != year && 2015 != year) {
			throw new IllegalArgumentException("cannot create a TSL for year (NEW): "
					+ year + " trimester " + trimester);
		}
		
		BigInteger tslSequenceNumber;
		DateTime listIssueDateTime;
		Document euTSLDocument;
		X509Certificate euSSLCertificate = null;
		

		// setup
		TrustServiceList trustServiceList = TrustServiceListFactory
				.newInstance();
		setupTSL(trustServiceList);
		
		// trust service provider list: certipost
		LOG.debug("Create TSP: Certipost");
		TrustServiceProvider certipostTrustServiceProvider = createTSP_certipost();
		LOG.debug("Add TSP_certipost to Trustlist");
		trustServiceList.addTrustServiceProvider(certipostTrustServiceProvider);
		
		// Certipost trust services: Root CA and Root CA2
		LOG.debug("Add Trustservice BRCA1 to TSP_Certipost");
		certipostTrustServiceProvider
			.addTrustService(createTSPService_BRCA1());
		LOG.debug("Add Trustservice BRCA2 to TSP_Certipost");
		certipostTrustServiceProvider
			.addTrustService(createTSPService_BRCA2());
		
		
		if (2010 == year) {
			switch (trimester) {
			case FIRST:
				tslSequenceNumber = BigInteger.valueOf(1);
				listIssueDateTime = new DateTime(2010, 1, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp.xml");
				break;
			case SECOND:
				tslSequenceNumber = BigInteger.valueOf(2);
				listIssueDateTime = new DateTime(2010, 5, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				break;
			case THIRD:
				tslSequenceNumber = BigInteger.valueOf(3);
				listIssueDateTime = new DateTime(2010, 9, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				break;
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		} else if (2011 == year) {
			// year == 2011
			switch (trimester) {
			case FIRST:
				tslSequenceNumber = BigInteger.valueOf(4);
				listIssueDateTime = new DateTime(2011, 1, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");
				break;
			case SECOND:
				tslSequenceNumber = BigInteger.valueOf(5);
				listIssueDateTime = new DateTime(2011, 5, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");
				break;
			case THIRD:
				tslSequenceNumber = BigInteger.valueOf(6);
				listIssueDateTime = new DateTime(2011, 9, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");
				break;
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		} else if (2012 == year) {
			// year == 2012
			switch (trimester) {
			case FIRST:
				tslSequenceNumber = BigInteger.valueOf(7);
				listIssueDateTime = new DateTime(2012, 1, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-2.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");
				break;
			case SECOND: {
				tslSequenceNumber = BigInteger.valueOf(8);
				listIssueDateTime = new DateTime(2012, 5, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				break;
			}
			case THIRD: {
				tslSequenceNumber = BigInteger.valueOf(9);
				listIssueDateTime = new DateTime(2012, 9, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");

				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				break;
			}
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		} else if (2013 == year) {
			switch (trimester) {
			case FIRST: {
				tslSequenceNumber = BigInteger.valueOf(10);
				listIssueDateTime = new DateTime(2013, 1, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");

				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				break;
			}
			case SECOND: {
				tslSequenceNumber = BigInteger.valueOf(11);
				listIssueDateTime = new DateTime(2013, 5, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.der");

				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				break;
			}
			case THIRD: {
				tslSequenceNumber = BigInteger.valueOf(12);
				listIssueDateTime = new DateTime(2013, 9, 1, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
				
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
				
				break;
			}
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		} else if (2014 == year){
			switch (trimester){
			case FIRST:{
				tslSequenceNumber = BigInteger.valueOf(16);
				listIssueDateTime = new DateTime(2014, 2, 14, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
				break;
			}case SECOND:{
				tslSequenceNumber = BigInteger.valueOf(17);
				listIssueDateTime = new DateTime(2014, 6, 20, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
				break;
			}case THIRD:{
				tslSequenceNumber = BigInteger.valueOf(19);
				listIssueDateTime = new DateTime(2014, 10, 9, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
                                
                                //Quovadis
                                LOG.debug("Create TSP Qua Vadis");
                                TrustServiceProvider quovadisTrustServiceProvider = createTSP_Quovadis();
                                LOG.debug("Add TSP_QuoVadis to Trustlist");
                                trustServiceList
                                        .addTrustServiceProvider(quovadisTrustServiceProvider);
                                LOG.debug("Add QuoVadis BE PKI CertificationAuthority");
                                quovadisTrustServiceProvider
                                        .addTrustService(createTSPService_QuoVadisBEPKICertificationAuthority());
                                
				break;
			}
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		}else if (2015 == year){
			switch (trimester){
			case FIRST:{
				tslSequenceNumber = BigInteger.valueOf(20);
				listIssueDateTime = new DateTime(2015, 2, 19, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
                                
                                //Quovadis
                                LOG.debug("Create TSP Qua Vadis");
                                TrustServiceProvider quovadisTrustServiceProvider = createTSP_Quovadis();
                                LOG.debug("Add TSP_QuoVadis to Trustlist");
                                trustServiceList
                                        .addTrustServiceProvider(quovadisTrustServiceProvider);
                                LOG.debug("Add QuoVadis BE PKI CertificationAuthority");
                                quovadisTrustServiceProvider
                                        .addTrustService(createTSPService_QuoVadisBEPKICertificationAuthority());
                                
				break;
			}
                        case SECOND:{
				tslSequenceNumber = BigInteger.valueOf(21);
				listIssueDateTime = new DateTime(2015, 6, 25, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
                                
                                //Quovadis
                                LOG.debug("Create TSP Qua Vadis");
                                TrustServiceProvider quovadisTrustServiceProvider = createTSP_Quovadis();
                                LOG.debug("Add TSP_QuoVadis to Trustlist");
                                trustServiceList
                                        .addTrustServiceProvider(quovadisTrustServiceProvider);
                                LOG.debug("Add QuoVadis BE PKI CertificationAuthority");
                                quovadisTrustServiceProvider
                                        .addTrustService(createTSPService_QuoVadisBEPKICertificationAuthority());
                                
				break;
			}
			case THIRD:{
				tslSequenceNumber = BigInteger.valueOf(22);
				listIssueDateTime = new DateTime(2015, 11, 5, 0, 0, 0, 0,
						DateTimeZone.UTC);
				euTSLDocument = loadDocumentFromResource("eu/tl-mp-33.xml");
				euSSLCertificate = loadCertificateFromResource("eu/ec.europa.eu.2013-2015.der");
		
				// BRCA 3 en BRCA 4
				LOG.debug("Add Trustservice BRCA3 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA3());
				LOG.debug("Add Trustservice BRCA4 to TSP_Certipost");
				certipostTrustServiceProvider.addTrustService(createTSPService_BRCA4());
				
				createTSPService_AdditionelServices_Certipost(certipostTrustServiceProvider);
				
				// SWIFT
				LOG.debug("Create TSP: Swift");	
				TrustServiceProvider swiftTrustServiceProvider = createTSP_swift();
				LOG.debug("Add TSP_swift to Trustlist");
				trustServiceList
						.addTrustServiceProvider(swiftTrustServiceProvider);
				LOG.debug("Add Trustservice SwiftNetPKI to TSP_Swift");
				swiftTrustServiceProvider
					.addTrustService(createTSPService_SWIFTNetPKI());
                                
                                //Quovadis
                                LOG.debug("Create TSP Qua Vadis");
                                TrustServiceProvider quovadisTrustServiceProvider = createTSP_Quovadis();
                                LOG.debug("Add TSP_QuoVadis to Trustlist");
                                trustServiceList
                                        .addTrustServiceProvider(quovadisTrustServiceProvider);
                                LOG.debug("Add QuoVadis BE PKI CertificationAuthority");
                                quovadisTrustServiceProvider
                                        .addTrustService(createTSPService_QuoVadisBEPKICertificationAuthority());
                                
				break;
			}
			default:
				throw new IllegalArgumentException(trimester.toString());
			}
		}else {
			throw new IllegalArgumentException("unsupported year");
		}
		
		
		//set sequencenumber
		trustServiceList.setTSLSequenceNumber(tslSequenceNumber);
		//set issuedate
		trustServiceList.setListIssueDateTime(listIssueDateTime);
		// next update
		int operationalOverlapWeeks = 2;
		DateTime nextUpdateDateTime = listIssueDateTime.plusMonths(12 / 3)
				.plusWeeks(operationalOverlapWeeks);
		trustServiceList.setNextUpdate(nextUpdateDateTime);
		
		trustServiceList
				.addOtherTSLPointer(
						"https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-hr.pdf",
						"application/pdf",
						"http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists",
						"EU",
						"European Commission",
						"http://uri.etsi.org/TrstSvc/TrustedList/schemerules/EUlistofthelists",
						Locale.ENGLISH,
						euSSLCertificate);

		TrustServiceList euTSL;
		try {
			euTSL = TrustServiceListFactory.newInstance(euTSLDocument);
		} catch (IOException e) {
			throw new RuntimeException("could not load EU trust list: "
					+ e.getMessage(), e);
		}
		X509Certificate euCertificate = euTSL.verifySignature();
		LOG.debug("EU certificate: " + euCertificate);

		
		trustServiceList
				.addOtherTSLPointer(
						"https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml",
						"application/vnd.etsi.tsl+xml",
						"http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists",
						"EU",
						"European Commission",
						"http://uri.etsi.org/TrstSvc/TrustedList/schemerules/EUlistofthelists",
						Locale.ENGLISH,
						euCertificate);
		
		/*
		Collections.sort(certipostTrustServiceProvider.getTrustServices(),new TrustServiceComparer());
		Collections.sort(trustServiceList.getTrustServiceProviders(), new TrustServiceProviderComparer());
		*/
		return trustServiceList;
	}
	
	private static void setupTSL(TrustServiceList trustServiceList){
		// scheme operator name
		trustServiceList
				.setSchemeOperatorName(
						"FPS Economy, SMEs, Self-employed and Energy - Quality and Safety",
						Locale.ENGLISH);
		trustServiceList
				.setSchemeOperatorName(
						"FOD Economie, KMO, Middenstand en Energie - Kwaliteit en Veiligheid",
						new Locale("nl"));
		trustServiceList
				.setSchemeOperatorName(
						"SPF Economie, PME, Classes moyennes et Energie - Qualité et Sécurité",
						Locale.FRENCH);
		trustServiceList
				.setSchemeOperatorName(
						"FÖD Wirtschaft, KMU, Mittelstand und Energie - Qualität und Sicherheit",
						Locale.GERMAN);

		// scheme operator postal address
		PostalAddressType schemeOperatorPostalAddress = new PostalAddressType();
		schemeOperatorPostalAddress
				.setStreetAddress("NG III - Koning Albert II-laan 16");
		schemeOperatorPostalAddress.setLocality("Brussels");
		schemeOperatorPostalAddress.setStateOrProvince("Brussels");
		schemeOperatorPostalAddress.setPostalCode("1000");
		schemeOperatorPostalAddress.setCountryName("BE"); // this one containing
		// an EU country
		// code
		trustServiceList.setSchemeOperatorPostalAddress(
				schemeOperatorPostalAddress, Locale.ENGLISH);

		schemeOperatorPostalAddress
				.setStreetAddress("NG III - Koning Albert II-laan 16");
		schemeOperatorPostalAddress.setLocality("Brussel");
		schemeOperatorPostalAddress.setStateOrProvince("Brussel");
		schemeOperatorPostalAddress.setPostalCode("1000");
		schemeOperatorPostalAddress.setCountryName("BE"); // this one containing
		// an EU country
		// code
		trustServiceList.setSchemeOperatorPostalAddress(
				schemeOperatorPostalAddress, new Locale("nl"));

		// scheme operator electronic address		
		trustServiceList.setSchemeOperatorElectronicAddresses(Locale.ENGLISH,
				"http://economie.fgov.be");
		trustServiceList.setSchemeOperatorElectronicAddresses(Locale.ENGLISH,
				"mailto:be.sign@economie.fgov.be");

		// scheme name
		trustServiceList
				.setSchemeName(
						"BE:Supervision/Accreditation Status List of certification services from Certification Service Providers, which are supervised/accredited by the referenced Scheme Operator's Member State for compliance with the relevant provisions laid down in Directive 1999/93/EC of the European Parliament and of the Council of 13 December 1999 on a Community framework for electronic signatures.",
						Locale.ENGLISH);

		// scheme information URIs
		trustServiceList.addSchemeInformationUri("http://tsl.belgium.be/",
				Locale.ENGLISH);
		// trustServiceList.addSchemeInformationUri("http://tsl.belgium.be/nl/",
		// new Locale("nl"));
		// trustServiceList.addSchemeInformationUri("http://tsl.belgium.be/fr/",
		// Locale.FRENCH);

		// status determination approach
		trustServiceList
				.setStatusDeterminationApproach(TrustServiceList.STATUS_DETERMINATION_APPROPRIATE);

		// scheme type
		/*
		 * The Scheme Type URIs can actually be visited. We should provide some
		 * information to ETSI for the BE schemerules.
		 */
		trustServiceList
			.addSchemeType(TrustServiceList.SCHEME_RULE_COMMON, Locale.ENGLISH);
		/*
		 * The BE schemerules MUSH be provided. We can add extra paths for
		 * language. For example: http://
		 * uri.etsi.org/TrstSvc/eSigDir-1999-93-EC-TrustedList/schemerules/BE/nl
		 */
		trustServiceList
				.addSchemeType("http://uri.etsi.org/TrstSvc/TrustedList/schemerules/BE", Locale.ENGLISH);

		// scheme territory
		trustServiceList.setSchemeTerritory("BE");

		// legal notice
		trustServiceList
				.addLegalNotice(
						"The applicable legal framework for the present TSL implementation of the Trusted List of supervised/accredited Certification Service Providers for Belgium is Directive 1999/93/EC of the European Parliament and of the Council of 13 December 1999 on a Community framework for electronic signatures and its implementation in Belgian laws. The applicable legal national framework is the Belgian CSP act of 9 July 2001 to create a legal framework for the usage of electronic signatures and certification services.",
						Locale.ENGLISH);
		
		// historical information period
				/*
				 * Volgens de wet van 9 JULI 2001. — Wet houdende vaststelling van
				 * bepaalde regels in verband met het juridisch kader voor elektronische
				 * handtekeningen en certificatiediensten: Bijlage II - punt i) alle
				 * relevante informatie over een gekwalificeerd certificaat te
				 * registreren gedurende de nuttige termijn van dertig jaar, in het
				 * bijzonder om een certificatiebewijs te kunnen voorleggen bij
				 * gerechtelijke procedures.
				 */
		trustServiceList.setHistoricalInformationPeriod(21845 * 3);
		
		trustServiceList
			.addDistributionPoint("http://tsl.belgium.be/tsl-be.xml");
	}
	
	private static TrustServiceProvider createTSP_certipost(){
		TrustServiceProvider certipostTrustServiceProvider = TrustServiceListFactory
				.createTrustServiceProvider("Certipost n.v./s.a.",
						"VATBE-0475396406", "Certipost s.a./n.v.");
		
		certipostTrustServiceProvider.addPostalAddress(Locale.ENGLISH,
				"Muntcentrum", "Brussels", "Brussels", "1000", "BE");
		certipostTrustServiceProvider.addElectronicAddress(Locale.ENGLISH, 
				"http://www.certipost.com/index.php?lang=en");
		certipostTrustServiceProvider.addElectronicAddress(Locale.ENGLISH,
				"mailto:eid.csp@certipost.be");
		certipostTrustServiceProvider.addInformationUri(Locale.ENGLISH, 
				"http://repository.eid.belgium.be");
		certipostTrustServiceProvider.addInformationUri(Locale.ENGLISH,
				"http://www.certipost.be/dpsolutions/en/e-certificates-legal-info.html");
		return certipostTrustServiceProvider;
	}
	
	private static TrustServiceProvider createTSP_swift(){
		TrustServiceProvider swiftTrustServiceProvider = TrustServiceListFactory
				.createTrustServiceProvider(
						"Society for Worldwide Interbank Financial Telecommunication SCRL",
						"VATBE-0413330856", "SWIFT");
		swiftTrustServiceProvider.addPostalAddress(Locale.ENGLISH,
				"Avenue Adèle 1", "La Hulpe", "Brussels", "1310",
				"BE");
		swiftTrustServiceProvider.addElectronicAddress(
				Locale.ENGLISH, "http://www.swift.com/");
		swiftTrustServiceProvider.addElectronicAddress(Locale.ENGLISH,
				"mailto:swift-pma@swift.com");
                swiftTrustServiceProvider.addInformationUri(Locale.ENGLISH,
				"http://www.swift.com/pkirepository");
                
                
		
		return swiftTrustServiceProvider;		
	}
	
      private static TrustServiceProvider createTSP_Quovadis(){
            TrustServiceProvider quovadisTrustServiceProvider = TrustServiceListFactory
                    .createTrustServiceProvider(
                            "QuoVadis Trustlink BVBA",
                            "VATBE-0537698318", 
                            "QuoVadis");
            quovadisTrustServiceProvider.addPostalAddress(Locale.ENGLISH,
                   "Capittelstraat 35", "Aarschot", "Vlaams-Brabant",  "3201", "BE");
            quovadisTrustServiceProvider.addElectronicAddress(Locale.ENGLISH, 
                    "http://www.quovadisglobal.be/");
            quovadisTrustServiceProvider.addElectronicAddress(Locale.ENGLISH, 
                    "mailto:info.be@quovadisglobal.com");
            quovadisTrustServiceProvider.addInformationUri(Locale.ENGLISH,
                    "https://www.quovadisglobal.be/~/media/Files/Repository/QV_RCA1_RCA3_CPCPS_V4_16.ashx");

            return quovadisTrustServiceProvider;
        }
        
	private static TrustService createTSPService_BRCA1(){
		
		X509Certificate rootCaCertificate = loadCertificateFromResource("eu/be/belgiumrca.crt");
		TrustService rootCaTrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI , TrustService.SERVICE_STATUS_UNDER_SUPERVISION, null, rootCaCertificate);
		rootCaTrustService.addOIDForQCSSCDStatusAsInCert("2.16.56.1.1.1.2.1",
				"urn:be:qc:natural:citizen");
		rootCaTrustService.addOIDForQCSSCDStatusAsInCert("2.16.56.1.1.1.7.1",
				"urn:be:qc:natural:foreigner");
		rootCaTrustService.addAdditionalServiceInformationUri(TrustService.SERVICE_TYPE_IDENTIFIER_ROOTCA_QC_URI);
		rootCaTrustService.addSchemeServiceDefinitionURI("http://tsl.belgium.be/pages/SchemeServiceDefinition");
		return rootCaTrustService;
		
	}
	
	private static TrustService createTSPService_BRCA2(){
		
		X509Certificate rootCa2Certificate = loadCertificateFromResource("eu/be/belgiumrca2.crt");
		TrustService rootCa2TrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION, null, rootCa2Certificate);
		rootCa2TrustService.addOIDForQCSSCDStatusAsInCert("2.16.56.9.1.1.2.1",
				"urn:be:qc:natural:citizen");
		rootCa2TrustService.addOIDForQCSSCDStatusAsInCert("2.16.56.9.1.1.7.1",
				"urn:be:qc:natural:foreigner");
		rootCa2TrustService.addAdditionalServiceInformationUri(TrustService.SERVICE_TYPE_IDENTIFIER_ROOTCA_QC_URI);
		rootCa2TrustService.addSchemeServiceDefinitionURI("http://tsl.belgium.be/pages/SchemeServiceDefinition");
		return rootCa2TrustService;
	}
	
	private static TrustService createTSPService_BRCA3(){
		
		X509Certificate rootCa3Certificate = loadCertificateFromResource("eu/be/belgiumrca3.crt");
		TrustService rootCa3TrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION, null, rootCa3Certificate);
		rootCa3TrustService.addOIDForQCSSCDStatusAsInCert(
				"2.16.56.10.1.1.2.1", "urn:be:qc:natural:citizen");
		rootCa3TrustService
				.addOIDForQCSSCDStatusAsInCert(
						"2.16.56.10.1.1.7.1",
						"urn:be:qc:natural:foreigner");
		
		rootCa3TrustService.addAdditionalServiceInformationUri(TrustService.SERVICE_TYPE_IDENTIFIER_ROOTCA_QC_URI);
		rootCa3TrustService.addSchemeServiceDefinitionURI("http://tsl.belgium.be/pages/SchemeServiceDefinition");
		
		return rootCa3TrustService;
	}
	
	private static TrustService createTSPService_BRCA4(){
		
		// Belgian Root CA 4
		X509Certificate rootCa4Certificate = loadCertificateFromResource("eu/be/belgiumrca4.crt");
		TrustService rootCa4TrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION, null, rootCa4Certificate);
		rootCa4TrustService.addOIDForQCSSCDStatusAsInCert(
				"2.16.56.12.1.1.2.1", "urn:be:qc:natural:citizen");
		rootCa4TrustService
				.addOIDForQCSSCDStatusAsInCert(
						"2.16.56.12.1.1.7.1",
						"urn:be:qc:natural:foreigner");
		rootCa4TrustService.addAdditionalServiceInformationUri(TrustService.SERVICE_TYPE_IDENTIFIER_ROOTCA_QC_URI);
		rootCa4TrustService.addSchemeServiceDefinitionURI("http://tsl.belgium.be/pages/SchemeServiceDefinition");
		return rootCa4TrustService;
	}
	
	
	private static void createTSPService_AdditionelServices_Certipost(TrustServiceProvider certipost){
		List<TrustService> additionalCertipostTrustServices = new LinkedList<TrustService>();
		
		X509Certificate caQS_BCT = loadCertificateFromResource("eu/be/certipost/Certipost Public CA for Qualified Signatures - BCT root signed.cer");
		X509Certificate caQS_VG = loadCertificateFromResource("eu/be/certipost/Certipost Public CA for Qualified Signatures - VG root signed.cer");
		TrustService caQS_TrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION,  null, caQS_VG, caQS_BCT);
		additionalCertipostTrustServices.add(caQS_TrustService);
		
		
		X509Certificate eTrustQCaCertificate = loadCertificateFromResource("eu/be/etrust/QCA_Self_Signed.crt");
		TrustService eTrustQCaTrustService = TrustServiceListFactory
				.createTrustService(null, TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION,  null, eTrustQCaCertificate);
		eTrustQCaTrustService.addOIDForQCForLegalPerson("0.3.2062.7.1.1.112.1");
		eTrustQCaTrustService.addOIDForQCForLegalPerson("0.3.2062.7.1.1.140.1");
		eTrustQCaTrustService.addOIDForQCForLegalPerson("0.3.2062.7.1.1.111.1");
		eTrustQCaTrustService.addAdditionalServiceInformationUri(TrustService.SERVICE_TYPE_IDENTIFIER_ROOTCA_QC_URI);
		additionalCertipostTrustServices.add(eTrustQCaTrustService);
		
		for (TrustService additionalCertipostTrustService : additionalCertipostTrustServices) {
			certipost
					.addTrustService(additionalCertipostTrustService);
		}
	} 
	
	private static TrustService createTSPService_SWIFTNetPKI(){
		X509Certificate swiftRootCertificate = loadCertificateFromResource("eu/be/swift/swiftnet_root.pem");
		TrustService swiftTrustService = TrustServiceListFactory
				.createTrustService(
						"SWIFTNet PKI Certification Authority",TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION,
						new DateTime(2013, 5, 15, 0, 0, 0, 0,
								DateTimeZone.UTC),
						swiftRootCertificate);
		swiftTrustService.addOIDForQCForLegalPerson(
				"1.3.21.6.3.10.200.3", true);
		return swiftTrustService;
	}
        
        private static TrustService createTSPService_QuoVadisBEPKICertificationAuthority(){
            X509Certificate quoVadisCertificate = loadCertificateFromResource("eu/be/quovadis/qvbecag1.cer");
            TrustService quoaVadisTrustService = TrustServiceListFactory
                    .createTrustService(
                            "QuoVadis BE PKI Certification Authority", TrustService.SERVICE_TYPE_IDENTIFIER_CA_QC_URI, TrustService.SERVICE_STATUS_UNDER_SUPERVISION, 
                            new DateTime(2014, 5, 20, 0, 0, 0, 0, DateTimeZone.UTC), quoVadisCertificate);
            
            return quoaVadisTrustService;
                    
        }
	 
	private static Document loadDocumentFromResource(String resourceName) {
		Thread currentThread = Thread.currentThread();
		ClassLoader classLoader = currentThread.getContextClassLoader();
		InputStream documentInputStream = classLoader
				.getResourceAsStream(resourceName);
		if (null == documentInputStream) {
			throw new IllegalArgumentException("resource not found: "
					+ resourceName);
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document tslDocument = documentBuilder.parse(documentInputStream);
			return tslDocument;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static X509Certificate loadCertificateFromResource(
			String resourceName) {
		Thread currentThread = Thread.currentThread();
		ClassLoader classLoader = currentThread.getContextClassLoader();
		InputStream certificateInputStream = classLoader
				.getResourceAsStream(resourceName);
		if (null == certificateInputStream) {
			throw new IllegalArgumentException(
					"could not load certificate resource: " + resourceName);
		}
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) certificateFactory
					.generateCertificate(certificateInputStream);
			return certificate;
		} catch (CertificateException e) {
			throw new RuntimeException("certificate factory error: "
					+ e.getMessage(), e);
		}
	}
}