/*
 * eID TSL Project.
 * Copyright (C) 2009 FedICT.
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

package be.fedict.eid.tsl.tool;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.fedict.eid.tsl.ChangeListener;
import be.fedict.eid.tsl.TrustService;
import be.fedict.eid.tsl.TrustServiceList;
import be.fedict.eid.tsl.TrustServiceProvider;

class TslInternalFrame extends JInternalFrame implements TreeSelectionListener,
		InternalFrameListener, ChangeListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(TslInternalFrame.class);

	private final TrustServiceList trustServiceList;

	private JTree tree;

	private JLabel serviceName;

	private JLabel serviceType;

	private JLabel serviceStatus;

	private JLabel serviceSha1Thumbprint;

	private JLabel serviceSha256Thumbprint;

	private JLabel validityBegin;

	private JLabel validityEnd;

	private final TslTool tslTool;

	private JLabel signer;

	private File tslFile;

	private JLabel signerSha1Fingerprint;

	private JLabel signerSha256Fingerprint;

	private X509Certificate signerCertificate;

	private JButton saveSignerCertificateButton;

	TslInternalFrame(File tslFile, TrustServiceList trustServiceList,
			TslTool tslTool) {
		super(tslFile.getName(), true, true, true);
		this.tslFile = tslFile;
		this.trustServiceList = trustServiceList;
		this.tslTool = tslTool;

		initUI();

		/*
		 * Keep us up-to-date on the changes on the TSL document.
		 */
		this.trustServiceList.addChangeListener(this);

		addInternalFrameListener(this);
		setSize(500, 300);
		setVisible(true);
	}

	TslInternalFrame(String name, TrustServiceList trustServiceList,
			TslTool tslTool) {
		super(name, true, true, true);
		this.trustServiceList = trustServiceList;
		this.tslTool = tslTool;

		initUI();

		/*
		 * Keep us up-to-date on the changes on the TSL document.
		 */
		this.trustServiceList.addChangeListener(this);

		addInternalFrameListener(this);
		setSize(500, 300);
		setVisible(true);
	}

	private void initUI() {
		JTabbedPane tabbedPane = new JTabbedPane();
		Container contentPane = this.getContentPane();
		contentPane.add(tabbedPane);

		addGenericTab(tabbedPane);
		addServiceProviderTab(tabbedPane);
		addSignatureTab(tabbedPane);
	}

	private void addSignatureTab(JTabbedPane tabbedPane) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		JPanel dataPanel = new JPanel(gridBagLayout);
		JPanel signaturePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tabbedPane.add("Signature", new JScrollPane(signaturePanel));
		signaturePanel.add(dataPanel);

		GridBagConstraints constraints = new GridBagConstraints();

		JLabel signerLabel = new JLabel("Signer");
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 10;
		dataPanel.add(signerLabel, constraints);
		this.signer = new JLabel();
		constraints.gridx++;
		dataPanel.add(this.signer, constraints);

		JLabel signerSha1FingerprintLabel = new JLabel(
				"Public key SHA1 fingerprint:");
		constraints.gridx = 0;
		constraints.gridy++;
		dataPanel.add(signerSha1FingerprintLabel, constraints);
		this.signerSha1Fingerprint = new JLabel();
		constraints.gridx++;
		dataPanel.add(this.signerSha1Fingerprint, constraints);

		JLabel signerSha256FingerprintLabel = new JLabel(
				"Public key SHA256 fingerprint:");
		constraints.gridx = 0;
		constraints.gridy++;
		dataPanel.add(signerSha256FingerprintLabel, constraints);
		this.signerSha256Fingerprint = new JLabel();
		constraints.gridx++;
		dataPanel.add(this.signerSha256Fingerprint, constraints);

		this.saveSignerCertificateButton = new JButton("Save Certificate...");
		constraints.gridx = 0;
		constraints.gridy++;
		dataPanel.add(this.saveSignerCertificateButton, constraints);
		this.saveSignerCertificateButton.addActionListener(this);
		this.saveSignerCertificateButton.setEnabled(false);

		updateView();
	}

	private void updateView() {
		this.signerCertificate = this.trustServiceList.verifySignature();
		if (null != this.signerCertificate) {
			this.signer.setText(this.signerCertificate
					.getSubjectX500Principal().toString());
			byte[] encodedPublicKey = this.signerCertificate.getPublicKey()
					.getEncoded();
			this.signerSha1Fingerprint.setText(DigestUtils
					.shaHex(encodedPublicKey));
			this.signerSha256Fingerprint.setText(DigestUtils
					.sha256Hex(encodedPublicKey));
			this.saveSignerCertificateButton.setEnabled(true);
		} else {
			this.signer.setText("[TSL is not signed]");
			this.signerSha1Fingerprint.setText("");
			this.signerSha256Fingerprint.setText("");
			this.saveSignerCertificateButton.setEnabled(false);
		}
	}

	private void addServiceProviderTab(JTabbedPane tabbedPane) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		tabbedPane.add("Service Providers", splitPane);

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				"Service Providers");
		this.tree = new JTree(rootNode);
		this.tree.addTreeSelectionListener(this);
		for (TrustServiceProvider trustServiceProvider : this.trustServiceList
				.getTrustServiceProviders()) {
			DefaultMutableTreeNode trustServiceProviderNode = new DefaultMutableTreeNode(
					trustServiceProvider.getName());
			rootNode.add(trustServiceProviderNode);
			for (TrustService trustService : trustServiceProvider
					.getTrustServices()) {
				MutableTreeNode trustServiceNode = new DefaultMutableTreeNode(
						trustService);
				trustServiceProviderNode.add(trustServiceNode);
			}
		}
		this.tree.expandRow(0);

		JScrollPane treeScrollPane = new JScrollPane(this.tree);
		JPanel detailsPanel = new JPanel();
		splitPane.setLeftComponent(treeScrollPane);
		splitPane.setRightComponent(detailsPanel);

		initDetailsPanel(detailsPanel);
	}

	private void initDetailsPanel(JPanel detailsPanel) {
		detailsPanel.setBorder(new TitledBorder("Details"));
		detailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		GridBagLayout gridBagLayout = new GridBagLayout();
		JPanel dataPanel = new JPanel(gridBagLayout);
		detailsPanel.add(dataPanel);

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 10;
		dataPanel.add(new JLabel("Service Name"), constraints);
		this.serviceName = new JLabel();
		constraints.gridx++;
		dataPanel.add(this.serviceName, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Service Type"), constraints);

		constraints.gridx++;
		this.serviceType = new JLabel();
		dataPanel.add(this.serviceType, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Service Status"), constraints);

		constraints.gridx++;
		this.serviceStatus = new JLabel();
		dataPanel.add(this.serviceStatus, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Service SHA1 Thumbprint"), constraints);

		constraints.gridx++;
		this.serviceSha1Thumbprint = new JLabel();
		dataPanel.add(this.serviceSha1Thumbprint, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Service SHA256 Thumbprint"), constraints);

		constraints.gridx++;
		this.serviceSha256Thumbprint = new JLabel();
		dataPanel.add(this.serviceSha256Thumbprint, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Validity begin"), constraints);

		constraints.gridx++;
		this.validityBegin = new JLabel();
		dataPanel.add(this.validityBegin, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Validity end"), constraints);

		constraints.gridx++;
		this.validityEnd = new JLabel();
		dataPanel.add(this.validityEnd, constraints);
	}

	private void addGenericTab(JTabbedPane tabbedPane) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		JPanel dataPanel = new JPanel(gridBagLayout);
		JPanel genericPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tabbedPane.add("Generic", new JScrollPane(genericPanel));
		genericPanel.add(dataPanel);

		GridBagConstraints constraints = new GridBagConstraints();

		JLabel schemeNameLabel = new JLabel("Scheme Name");
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 10;
		dataPanel.add(schemeNameLabel, constraints);
		JLabel schemeName = new JLabel(this.trustServiceList.getSchemeName());
		constraints.gridx++;
		dataPanel.add(schemeName, constraints);

		JLabel schemeOperatorNameLabel = new JLabel("Scheme Operator Name");
		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(schemeOperatorNameLabel, constraints);
		JLabel schemeOperatorName = new JLabel(
				this.trustServiceList.getSchemeOperatorName());
		constraints.gridx++;
		dataPanel.add(schemeOperatorName, constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Type"), constraints);
		constraints.gridx++;
		dataPanel.add(
				new JLabel(this.trustServiceList.getType().substring(
						this.trustServiceList.getType().indexOf("TSLType/")
								+ "TSLType/".length())), constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Sequence number"), constraints);
		constraints.gridx++;
		dataPanel.add(new JLabel(this.trustServiceList.getSequenceNumber()
				.toString()), constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Issue date"), constraints);
		constraints.gridx++;
		dataPanel.add(new JLabel(this.trustServiceList.getIssueDate()
				.toString()), constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("Next update"), constraints);
		constraints.gridx++;
		dataPanel.add(new JLabel(this.trustServiceList.getNextUpdate()
				.toString()), constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("TSL SHA1 fingerprint"), constraints);
		constraints.gridx++;
		dataPanel.add(new JLabel(getSha1Fingerprint()), constraints);

		constraints.gridy++;
		constraints.gridx = 0;
		dataPanel.add(new JLabel("TSL SHA256 fingerprint"), constraints);
		constraints.gridx++;
		dataPanel.add(new JLabel(getSha256Fingerprint()), constraints);
	}

	private String getSha1Fingerprint() {
		return this.trustServiceList.getSha1Fingerprint();
	}

	private String getSha256Fingerprint() {
		return this.trustServiceList.getSha256Fingerprint();
	}

	public TrustServiceList getTrustServiceList() {
		return this.trustServiceList;
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (treeNode.isLeaf()) {
			TrustService trustService = (TrustService) treeNode.getUserObject();
			this.serviceName.setText(trustService.getName());
			this.serviceType.setText(trustService.getType().substring(
					trustService.getType().indexOf("Svctype/")
							+ "Svctype/".length()));
			this.serviceStatus.setText(trustService.getStatus().substring(
					trustService.getStatus().indexOf("Svcstatus/")
							+ "Svcstatus/".length()));
			X509Certificate certificate = trustService
					.getServiceDigitalIdentity();
			byte[] encodedCertificate;
			try {
				encodedCertificate = certificate.getEncoded();
			} catch (CertificateEncodingException e) {
				throw new RuntimeException("cert: " + e.getMessage(), e);
			}
			String sha1Thumbprint = DigestUtils.shaHex(encodedCertificate);
			this.serviceSha1Thumbprint.setText(sha1Thumbprint);

			String sha256Thumbprint = DigestUtils.sha256Hex(encodedCertificate);
			this.serviceSha256Thumbprint.setText(sha256Thumbprint);

			this.validityBegin.setText(certificate.getNotBefore().toString());
			this.validityEnd.setText(certificate.getNotAfter().toString());
		} else {
			this.serviceName.setText("");
			this.serviceType.setText("");
			this.serviceStatus.setText("");
			this.serviceSha1Thumbprint.setText("");
			this.serviceSha256Thumbprint.setText("");
			this.validityBegin.setText("");
			this.validityEnd.setText("");
		}
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		LOG.debug("activated: " + e.getInternalFrame().getTitle());
		this.tslTool.setActiveTslInternalFrame(this);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		LOG.debug("closed");
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		LOG.debug("closing");
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		LOG.debug("deactivated: " + e.getInternalFrame().getTitle());
		this.tslTool.setActiveTslInternalFrame(null);
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		LOG.debug("deiconified");
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		LOG.debug("iconified");
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		LOG.debug("opened");
	}

	@Override
	public void changed() {
		LOG.debug("TSL changed");
		if (null != this.tslFile) {
			setTitle("*" + this.tslFile.getName());
		}
		this.tslTool.setChanged(true);
		updateView();
	}

	public File getFile() {
		return this.tslFile;
	}

	public void save() throws IOException {
		this.trustServiceList.saveAs(this.tslFile);
		setTitle(this.tslFile.getName());
	}

	public void saveAs(File tslFile) throws IOException {
		this.tslFile = tslFile;
		save();
	}

	public void export(File pdfFile) throws IOException {
		LOG.debug("exporting to PDF: " + pdfFile.getAbsolutePath());
		this.trustServiceList.humanReadableExport(pdfFile);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Signer Certificate");
		int result = fileChooser.showSaveDialog(this);
		if (JFileChooser.APPROVE_OPTION == result) {
			File file = fileChooser.getSelectedFile();
			if (file.exists()) {
				int confirmResult = JOptionPane.showConfirmDialog(this,
						"File already exists.\n" + file.getAbsolutePath()
								+ "\n" + "Overwrite file?", "Overwrite",
						JOptionPane.OK_CANCEL_OPTION);
				if (JOptionPane.CANCEL_OPTION == confirmResult) {
					return;
				}
			}
			try {
				FileUtils.writeByteArrayToFile(file,
						this.signerCertificate.getEncoded());
			} catch (Exception e) {
				throw new RuntimeException("error writing file: "
						+ e.getMessage(), e);
			}
		}
	}
}
