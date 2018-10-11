package com.shuishou.digitalmenu.importtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class MainFrame extends JFrame {

	private JTextField tfFileName = new JTextField();
	private JFileChooser fc = new JFileChooser();
	private JButton btnSubmit = new JButton("Submit");
	private JButton btnChoose = new JButton("Choose");
	private JLabel lbStatus = new JLabel();
	
	private List<String> sqls = new ArrayList<String>();
	public MainFrame(){
		initUI();
		
	}
	
	private void executeDB() throws Exception{
		File file = new File("dbproperties.properties");
		Reader reader = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			reader = new FileReader(file);
			Properties ps = new Properties();
			ps.load(reader);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(ps.getProperty("db"), ps.getProperty("username"), ps.getProperty("password"));
			stmt = conn.createStatement();
			for (int i = 0; i < sqls.size(); i++) {
				System.out.println(sqls.get(i));
				stmt.execute(sqls.get(i));
				lbStatus.setText("execute " + (i+1) + "/" + sqls.size() +" sentences...");
			}
		} catch (Exception e1) {
			throw e1;
		} finally{
			try {
				if (reader != null)
					reader.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void initUI(){
		tfFileName.setEditable(false);
		lbStatus.setSize(100, 25);
		lbStatus.setText("status bar...");
		JPanel pContent = new JPanel(new GridBagLayout());
		pContent.add(tfFileName, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		pContent.add(btnChoose, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 20), 0, 0));
		pContent.add(btnSubmit, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		
		pContent.setBorder(BorderFactory.createLineBorder(Color.red));
		tfFileName.setBorder(BorderFactory.createLineBorder(Color.green));
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pContent, BorderLayout.CENTER);
		c.add(lbStatus, BorderLayout.SOUTH);
		
		btnChoose.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				File workingDir = new File(System.getProperty("user.dir"));
				fc.setCurrentDirectory(workingDir);
				fc.setMultiSelectionEnabled(false);
//				fc.setFileFilter(new ImportFileFilter("csv"));
//				fc.setFileFilter(new ImportFileFilter("xls"));
				fc.setFileFilter(new ImportFileFilter("xlsx"));
				fc.showDialog(MainFrame.this, "Choose");
				File file = fc.getSelectedFile();
				if (file != null)
					tfFileName.setText(file.getAbsolutePath());
			}
		});
		
		btnSubmit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				doSubmit();
				
			}}
		);
	}
	
	private void doSubmit(){
		File file = fc.getSelectedFile();
		if (file == null)
			return;
		try {
			readFileBuildSQL();
			executeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void readFileBuildSQL() throws Exception{
		if (fc.getSelectedFile() == null)
			return;
		sqls.clear();
//		sqls.add("delete from dishchoose_popinfo");
//		sqls.add("delete from dishchoose_subitem");
//		sqls.add("delete from dishchoose_subitem");
//		sqls.add("delete from dishconfig");
//		sqls.add("delete from dishconfiggroup");
//		sqls.add("delete from dish_dishconfiggroup");
//		sqls.add("delete from dish;");
//		sqls.add("delete from category2_printer");
//		sqls.add("delete from category2;");
//		sqls.add("delete from category1;");
		try {
			Workbook wb = WorkbookFactory.create(fc.getSelectedFile());
//			XSSFSheet sheetC1 = (XSSFSheet) wb.getSheetAt(0);
//			for (int i = 1; i < sheetC1.getPhysicalNumberOfRows(); i++) {
//				XSSFRow row = sheetC1.getRow(i);
//				String sql = "insert into category1(id, first_language_name, second_language_name, sequence) values (";
//				sql += (int)row.getCell(0).getNumericCellValue() + ",";
//				sql += "'" + row.getCell(1).getStringCellValue() + "',";
//				sql += "'" + row.getCell(2).getStringCellValue() + "',";
//				sql += (int)row.getCell(3).getNumericCellValue() +");";
//				sqls.add(sql);
//			}
//			XSSFSheet sheetC2 = (XSSFSheet) wb.getSheetAt(1);
//			for (int i = 1; i < sheetC2.getPhysicalNumberOfRows(); i++) {
//				XSSFRow row = sheetC2.getRow(i);
//				String sql = "insert into category2(id, first_language_name, second_language_name, sequence, category1_id) values (";
//				sql += (int)row.getCell(0).getNumericCellValue() + ",";
//				sql += "'" + row.getCell(1).getStringCellValue() + "',";
//				sql += "'" + row.getCell(2).getStringCellValue() + "',";
//				sql += (int)row.getCell(3).getNumericCellValue() +",";
//				sql += (int)row.getCell(4).getNumericCellValue() +");";
//				sqls.add(sql);
//			}
			XSSFSheet sheetDish = (XSSFSheet) wb.getSheetAt(2);
			for (int i = 1; i < sheetDish.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = sheetDish.getRow(i);
				String sql = "insert into dish(id, first_language_name, second_language_name, sequence, category2_id, price, isNew, isSpecial, hotLevel, isSoldOut, "
						+ "abbreviation, choose_mode, automerge_whilechoose, purchaseType, allowFlavor, isPromotion, originPrice ) values (";
				if (row.getCell(0) == null)
					break; //XSSFSheet is stupid, always get null row
				sql += (int)row.getCell(0).getNumericCellValue() + ",";//id
				//first_language_name
				String firstname = row.getCell(1).getStringCellValue();
				firstname = firstname.replaceAll("'", "''").replaceAll("\"", "\"\"");
				sql += "'" + firstname + "',";
				//second_language_name
				if (row.getCell(2) != null){
					String secondname = row.getCell(2).getStringCellValue();
					secondname = secondname.replaceAll("'", "''").replaceAll("\"", "\"\"");
					sql += "'" + secondname + "',";
				} else 
					sql += "'', ";
				//sequence
				if (row.getCell(3) != null)
					sql += (int)row.getCell(3).getNumericCellValue() + ",";
				else 
					sql += "'',";
				//category2_id
				sql += (int)row.getCell(4).getNumericCellValue() + ",";
				//price
				sql += row.getCell(5).getNumericCellValue() + ",";
				//isNew
				sql += (int)row.getCell(6).getNumericCellValue() + ",";
				//isSpecial
				sql += (int)row.getCell(7).getNumericCellValue() + ",";
				//hotlevel
				sql += (int)row.getCell(8).getNumericCellValue() + ",";
				//isSoldout
				sql += "0,";
				//abbreviation
				sql += "'" + row.getCell(10).getStringCellValue() + "',";
				//choose_mode
				sql += "'" + row.getCell(11).getNumericCellValue()+ "',";
				//automerge_whilechoose
				sql += (int)row.getCell(12).getNumericCellValue()+ ",";
				sql += (int)row.getCell(13).getNumericCellValue()+ ",";//purchaseType
				sql += (int)row.getCell(14).getNumericCellValue()+ ",";//allowFlavor
				sql += (int)row.getCell(15).getNumericCellValue()+ ",";//isPromotion
				sql += "0)";//originPrice
				sqls.add(sql);
			}
			sqls.add("commit");
		} catch (Exception e) {
			throw e;
		} 
	}
	
	public static void main(String[] args){
		MainFrame f = new MainFrame();
		f.setSize(new Dimension(300, 300));
		f.setResizable(false);
		f.setLocation(500, 500);
		f.setTitle("Import Menu Tool");
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.setVisible(true);
		
	}
}
