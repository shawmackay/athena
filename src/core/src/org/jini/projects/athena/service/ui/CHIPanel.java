/*
 * CHIPanel.java
 * 
 * Created on 30 August 2002, 11:17
 */

package org.jini.projects.athena.service.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import org.jini.projects.athena.command.dialect.TransformInfo;
import org.jini.projects.athena.command.types.ArrayType;
import org.jini.projects.athena.command.types.ObjectType;
import org.jini.projects.athena.command.types.ScalarType;
import org.jini.projects.athena.service.AthenaAdmin;

/**
 * @author Internet
 */
public class CHIPanel extends javax.swing.JPanel {
	DefaultListModel typesdlm = new DefaultListModel();
	DefaultListModel handlersdlm = new DefaultListModel();
	DefaultListModel handlerCommandsdlm = new DefaultListModel();
	HashMap map = null;
	Map handlers = null;
	Map dialects = null;
	// JTextArea handlerSrcEd = new JTextArea();
	SyntaxPanel synpanel = new SyntaxPanel();

	public CHIPanel(AthenaAdmin athreg) {
		try {
			handlers = athreg.getHandlerDetails();
			for (Iterator handlersIter = handlers.entrySet().iterator(); handlersIter.hasNext();) {
				Map.Entry entry = (Entry) handlersIter.next();
				handlersdlm.addElement(entry.getKey());
			}
			map = (HashMap) athreg.getTypeInformation();
			dialects = athreg.getDialectDetails();
			Set set = map.entrySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				System.out.println("Class : " + entry.getValue().getClass().getName());
				typesdlm.addElement((String) entry.getKey());
			}
		} catch (Exception e) {
			System.out.println("An Error occured whilst trying to obtain type information");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		initComponents();
		jPanel9.setLayout(new BorderLayout());
		jPanel9.add(tpa);
		jPanel10.setLayout(new BorderLayout());
		jPanel10.add(tpo);
		jPanel11.setLayout(new BorderLayout());
		jPanel11.add(tpe);
		jPanel12.setLayout(new BorderLayout());
		jPanel12.add(tpt, BorderLayout.CENTER);
	}

	TypesPanelArray tpa = new TypesPanelArray();
	TypesPanelObject tpo = new TypesPanelObject();
	TypesPanelExpression tpe = new TypesPanelExpression();
	TypesPanelTransform tpt = new TypesPanelTransform();

	/** Creates new form CHIPanel */
	public CHIPanel() {
		initComponents();
		jPanel9.setLayout(new BorderLayout());
		jPanel9.add(tpa);
		jPanel10.setLayout(new BorderLayout());
		jPanel10.add(tpo);
		jPanel11.setLayout(new BorderLayout());
		jPanel11.add(tpe);
		jPanel12.setLayout(new BorderLayout());
		jPanel12.add(tpt);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;
		buttonGroup1 = new javax.swing.ButtonGroup();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		handlersPanel = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		handlerList = new javax.swing.JList();
		jPanel5 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();
		jPanel6 = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jTextField2 = new javax.swing.JTextField();
		jTextField3 = new javax.swing.JTextField();
		jPanel7 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		handlerCommandsList = new javax.swing.JList();
		jTextArea1 = new javax.swing.JTextArea();
		typePanel = new javax.swing.JPanel();
		jScrollPane11 = new javax.swing.JScrollPane();
		typesList = new javax.swing.JList();
		typeInfoPanel = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jTextField4 = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		jTextField41 = new javax.swing.JTextField();
		jPanel9 = new javax.swing.JPanel();
		jPanel10 = new javax.swing.JPanel();
		jPanel11 = new javax.swing.JPanel();
		jPanel12 = new javax.swing.JPanel();
		dialectPanel = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		setLayout(new java.awt.GridBagLayout());
		handlersPanel.setLayout(new BorderLayout());
		handlerList.setModel(handlersdlm);
		handlerList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// TODO Complete method stub for valueChanged
				//.setText((String)
				// handlers.get(handlerList.getSelectedValue()));
				try {
					synpanel.insertDoc((String) handlers.get(handlerList.getSelectedValue()));
				} catch (BadLocationException e1) {
					// URGENT Handle BadLocationException
					e1.printStackTrace();
				}
			}
		});
		Border b = new TitledBorder(new EtchedBorder(), "Available Handlers");
		Border b1 = new TitledBorder(new EtchedBorder(), "Handler Source");
		jScrollPane1.setBorder(b);
		jScrollPane1.setViewportView(handlerList);
		handlerList.setBackground(new java.awt.Color(224, 224, 255));
		handlerList.setFont(new Font("Dialog", Font.PLAIN, 18));
		JScrollPane handlerSrcView = new JScrollPane();
		handlerSrcView.setBorder(b1);
		handlerSrcView.setViewportView(synpanel);
		JSplitPane p = new JSplitPane();
		p.setLeftComponent(jScrollPane1);
		p.setRightComponent(handlerSrcView);
		p.setOneTouchExpandable(true);
		p.setDividerLocation(200);
		handlersPanel.add(p, BorderLayout.CENTER);
		//
		//        jPanel5.setLayout(new java.awt.GridBagLayout());
		//
		//        jLabel1.setText("Name:");
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 0;
		//        gridBagConstraints.gridy = 0;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
		//        jPanel5.add(jLabel1, gridBagConstraints);
		//
		//        jLabel3.setText("Parameters");
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 0;
		//        gridBagConstraints.gridy = 1;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
		//        jPanel5.add(jLabel3, gridBagConstraints);
		//
		//        jComboBox1.setFont(new java.awt.Font("Dialog", 0, 12));
		//        jComboBox1.setForeground(java.awt.Color.blue);
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 1;
		//        gridBagConstraints.gridy = 1;
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
		//        jPanel5.add(jComboBox1, gridBagConstraints);
		//
		//        jPanel6.setLayout(new java.awt.GridBagLayout());
		//
		//        jPanel6.setBorder(new javax.swing.border.TitledBorder("Parameter
		// Info"));
		//        jLabel4.setText("Validation");
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
		//        jPanel6.add(jLabel4, gridBagConstraints);
		//
		//        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		//        jLabel6.setText("Type");
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 0;
		//        gridBagConstraints.gridy = 1;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
		//        jPanel6.add(jLabel6, gridBagConstraints);
		//
		//        jTextField1.setBackground(new java.awt.Color(204, 204, 255));
		//        jTextField1.setEditable(false);
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//        gridBagConstraints.ipadx = 120;
		//        gridBagConstraints.weightx = 1.0;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
		//        jPanel6.add(jTextField1, gridBagConstraints);
		//
		//        jTextField2.setBackground(new java.awt.Color(204, 204, 255));
		//        jTextField2.setEditable(false);
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 1;
		//        gridBagConstraints.gridy = 1;
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//        gridBagConstraints.weightx = 1.0;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 12);
		//        jPanel6.add(jTextField2, gridBagConstraints);
		//
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 1;
		//        gridBagConstraints.gridy = 2;
		//        jPanel5.add(jPanel6, gridBagConstraints);
		//
		//        jTextField3.setBackground(new java.awt.Color(204, 204, 255));
		//        jTextField3.setEditable(false);
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 1;
		//        gridBagConstraints.gridy = 0;
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
		//        jPanel5.add(jTextField3, gridBagConstraints);
		//
		//        jPanel7.setLayout(new java.awt.GridBagLayout());
		//
		//        jPanel7.setBorder(new javax.swing.border.TitledBorder("Commands"));
		//        jScrollPane2.setBorder(new javax.swing.border.EtchedBorder());
		//        handlerCommandsList.setModel(handlerCommandsdlm);
		//        handlerCommandsList.addListSelectionListener(new
		// javax.swing.event.ListSelectionListener() {
		//            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
		//                jList2ValueChanged(evt);
		//            }
		//        });
		//
		//        jScrollPane2.setViewportView(handlerCommandsList);
		//
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 0;
		//        gridBagConstraints.gridy = 0;
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		//        gridBagConstraints.ipadx = 180;
		//        gridBagConstraints.ipady = 80;
		//        gridBagConstraints.weightx = 1.0;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
		//        jPanel7.add(jScrollPane2, gridBagConstraints);
		//
		//        jTextArea1.setLineWrap(true);
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 0;
		//        gridBagConstraints.gridy = 1;
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		//        gridBagConstraints.ipady = 50;
		//        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
		//        jPanel7.add(jTextArea1, gridBagConstraints);
		//
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.gridx = 1;
		//        gridBagConstraints.gridy = 3;
		//        jPanel5.add(jPanel7, gridBagConstraints);
		//
		//        gridBagConstraints = new java.awt.GridBagConstraints();
		//        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		//        gridBagConstraints.weightx = 1.0;
		//        handlersPanel.add(jPanel5, gridBagConstraints);
		jTabbedPane1.addTab("Handlers", handlersPanel);
		typePanel.setLayout(new BorderLayout());
		typesList.setModel(typesdlm);
		typesList.setBackground(new java.awt.Color(224, 224, 255));
		typesList.setFont(new Font("Dialog", Font.PLAIN, 18));
		typesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
				typesListValueChanged(evt);
			}
		});
		jScrollPane11.setViewportView(typesList);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 6);
		gridBagConstraints.weightx = 1.2;
		gridBagConstraints.weighty = 1.0;
		//typePanel.add(jScrollPane11, gridBagConstraints);
		typeInfoPanel.setLayout(new java.awt.GridBagLayout());
		typeBorder = new javax.swing.border.TitledBorder("DataType Information");
		typeInfoPanel.setBorder(typeBorder);
		arrborder = new javax.swing.border.TitledBorder("Array Details");
		jPanel9.setBorder(arrborder);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		typeInfoPanel.add(jPanel9, gridBagConstraints);
		objborder = new javax.swing.border.TitledBorder("Object Details");
		jPanel10.setBorder(objborder);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		typeInfoPanel.add(jPanel10, gridBagConstraints);
		expborder = new javax.swing.border.TitledBorder("Expression Details");
		jPanel11.setBorder(expborder);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		typeInfoPanel.add(jPanel11, gridBagConstraints);
		tranborder = new javax.swing.border.TitledBorder("Transform Details");
		jPanel12.setBorder(tranborder);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		typeInfoPanel.add(jPanel12, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		JSplitPane sp = new JSplitPane();
		sp.setLeftComponent(jScrollPane11);
		sp.setRightComponent(typeInfoPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		typePanel.add(sp);
		jTabbedPane1.addTab("Types", typePanel);
		setUpDialectPanel();
		jTabbedPane1.addTab("XSL Dialects", null, dialectPanel, "null");
		jPanel4.setEnabled(false);
		//  jTabbedPane1.addTab("Unused", jPanel4);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jTabbedPane1, gridBagConstraints);
	}

	private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {
		jTextArea1.setText((String) handlerCommandsList.getSelectedValue());
	}

	// Variables declaration - do not modify
	private javax.swing.JPanel jPanel9;
	private javax.swing.JScrollPane jScrollPane11;
	private javax.swing.JPanel typeInfoPanel;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel dialectPanel;
	private javax.swing.JPanel typePanel;
	private javax.swing.JPanel handlersPanel;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JPanel jPanel12;
	private javax.swing.JPanel jPanel11;
	private javax.swing.JPanel jPanel10;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTextField jTextField4;
	private javax.swing.JTextField jTextField3;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JList handlerCommandsList;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JList handlerList;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JList typesList;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JTextField jTextField41;
	// End of variables declaration
	private TitledBorder typeBorder;
	private TitledBorder tranborder;
	private TitledBorder arrborder;
	private TitledBorder expborder;
	private TitledBorder objborder;
	private DefaultListModel dialectsModel;
	private SyntaxPanel insyntax;
	private SyntaxPanel outsyntax;
	private JList dialectsList;

	public static void main(String[] args) {
		javax.swing.JFrame fr = new javax.swing.JFrame();
		fr.getContentPane().add(new CHIPanel());
		fr.pack();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.show();
	}

	private void typesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
		// Add your handling code here:
		tpt.dlm.clear();
		tpo.dlm.clear();
		String typeselect = (String) typesdlm.get(typesList.getSelectedIndex());
		typeBorder.setTitle("Datatype Information for " + typeselect);
		objborder.setTitleColor(Color.BLACK);
		expborder.setTitleColor(Color.BLACK);
		tranborder.setTitleColor(Color.BLACK);
		arrborder.setTitleColor(Color.BLACK);
		tpa.clear();
		tpo.clear();
		tpt.dlm.clear();
		tpe.jTextField1.setText("");
		tpe.jTextField2.setText("");
		if (map.containsKey(typeselect)) {
			Object x = map.get(typeselect);
			if (x instanceof ScalarType) {
				ScalarType simp = (ScalarType) x;
				if (simp.isExpression()) {
					expborder.setTitleColor(Color.WHITE);
					tpe.jTextField1.setText(simp.getExpression());
				}
				if (simp.getTransformType_Name() != null) {
					tranborder.setTitleColor(Color.WHITE);
					tpt.jTextField1.setText((String) simp.getTransformType_Name());
					Object[] arr = simp.getAllowableTransforms();
					for (int i = 0; i < arr.length; i++)
						tpt.dlm.addElement(arr[i].toString());
				}
			}
			if (x instanceof ObjectType) {
				objborder.setTitleColor(Color.WHITE);
				ObjectType ct = (ObjectType) x;
				Map map = ct.getFields();
				Set st = map.keySet();
				Object[] arr = st.toArray();
				for (int i = 0; i < arr.length; i++) {
					tpo.dlm.addElement(arr[i].toString());
				}
			}
			if (x instanceof ArrayType) {
				ArrayType arrt = (ArrayType) x;
				arrborder.setTitleColor(Color.WHITE);
				tpa.setData(arrt.getBase(), arrt.getRepeater(), arrt.getSeparator(), arrt.getTail());
			}
		}
		typeInfoPanel.updateUI();
	}

	public void updateDialectViews(ListSelectionEvent evt) {
		String dialectselect = (String) dialectsModel.get(dialectsList.getSelectedIndex());
		TransformInfo ti = (TransformInfo) ((Map) dialects.get("transforms")).get(dialectselect);
		Map in = (Map) dialects.get("in");
		Map out = (Map) dialects.get("out");
		String xsl_in = (String) in.get(ti.getInputTransForm());
		String xsl_out = (String) out.get(ti.getOutputTransform());
		try {
			insyntax.insertDoc(xsl_in);
			outsyntax.insertDoc(xsl_out);
		} catch (BadLocationException e) {
			// URGENT Handle BadLocationException
			e.printStackTrace();
		}
	}

	public void setUpDialectPanel() {
		dialectPanel.setLayout(new BorderLayout());
		dialectsList = new JList();
		dialectsModel = new DefaultListModel();
		Map l = (Map) dialects.get("transforms");
		Iterator iter = l.keySet().iterator();
		while (iter.hasNext()) {
			String ti = (String) iter.next();
			dialectsModel.addElement(ti);
		}
		dialectsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
				updateDialectViews(evt);
			}
		});
		dialectsList.setModel(dialectsModel);
		dialectsList.setBackground(new java.awt.Color(224, 224, 255));
		dialectsList.setFont(new Font("Dialog", Font.PLAIN, 18));
		// dialectPanel.add(dialectsList, BorderLayout.WEST);
		JPanel transformsPanel = new JPanel();
		transformsPanel.setLayout(new BorderLayout());
		insyntax = new SyntaxPanel();
		insyntax.setBorder(new TitledBorder(new EtchedBorder(), "Input Transform"));
		outsyntax = new SyntaxPanel();
		outsyntax.setBorder(new TitledBorder(new EtchedBorder(), "Output Transform"));
		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitpane.setLeftComponent(insyntax);
		splitpane.setRightComponent(outsyntax);
		transformsPanel.add(splitpane, BorderLayout.CENTER);
		JSplitPane sp = new JSplitPane();
		JScrollPane listScroll = new JScrollPane(dialectsList);
		listScroll.setBorder(new TitledBorder(new EtchedBorder(), "Available Dialects"));
		sp.setLeftComponent(listScroll);
		sp.setRightComponent(transformsPanel);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(200);
		dialectPanel.add(sp, BorderLayout.CENTER);
	}
}