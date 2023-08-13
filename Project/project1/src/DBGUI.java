import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// main GUI part
public class DBGUI extends Application {

// data members
	MetaData md = new MetaData();
	private Connection con = MyConnection.makeConnection();
	private TableView table;
	TreeView<String> tree;
	Button[] buttons;
	Label[] labels;
	TextField[] txt;
	TextArea txtArea; 
	private final String[] btntext = { "clear", "save", "update", "delete", "print", "search" };

	
// function members
	private HBox addCenterPane() {
		
		HBox hb1 = new HBox();
		
		// Add TableView
		VBox vb = new VBox();

		table = new TableView<>();
		table.setMinSize(700, 150);
		table.setMaxSize(700, 150);
		table.setStyle("-fx-border-color: Black;");
		table.prefWidthProperty().bind(vb.prefWidthProperty());
		//table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		//table.getSelectionModel().setCellSelectionEnabled(false);
		
		// If click menu => 
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
			if(oldVal != newVal)
				showFields();
		});
		
		// Add Labels and TextFields
		GridPane  gp = new GridPane (); 
		gp.setPadding(new Insets(15, 15, 15, 125));
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setStyle("-fx-border-color: Blue;");
		gp.prefHeightProperty().bind(table.prefWidthProperty());
		  txt=new TextField[10]; 
		  labels= new Label[10];
		  
		  for (int i = 0; i < labels.length; i++) { 
			  labels[i]= new Label("Label..");
			  labels[i].setMinSize(150, 25);
		      txt[i]= new TextField(" Text.. "); 
		      txt[i].setMinSize(300, 20);
		      gp.addRow(i, labels[i],txt[i] );
		      labels[i].prefHeightProperty().bind(gp.widthProperty());
		      txt[i].prefHeightProperty().bind(gp.widthProperty());
		   }
	 
		  
		 vb.getChildren().addAll(table, gp);
		
		 // Add TreeView
				StackPane stack = new StackPane();

				// Create the tree
				
				tree = addNodestoTree();
				tree.setShowRoot(true);
				
			
				tree.setMaxWidth(150);
				tree.prefWidthProperty().bind(stack.prefWidthProperty());
				
				/* 1. GUI 컴퍼넌트 넣기 */
				tree.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue)->{
					if(newValue != oldValue) {
						String str=mySelectedNode();
						txtArea.appendText("You have selected " + str + "\n");
					
						if(str.equals(Nodes.Channel.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.donate.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Funding.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.LogTable.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Member.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Plan_.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Project.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Subscribe.toString())) { rsToTableView(str); }
						if(str.equals(Nodes.Exit.toString())) { System.out.println("Exit javafx program."); System.exit(0); }
						if(str.equals(Nodes.About.toString())) {
							md.getStage();
						}

					}
					
				});
				
				stack.getChildren().add(tree);

		hb1.getChildren().addAll(stack,vb);
		hb1.setStyle("-fx-border-color:black;");
		hb1.setSpacing(20);
		hb1.prefHeightProperty().bind(vb.prefWidthProperty());
	

		return hb1;
	}

	
	private StackPane addBottomPane() {

		StackPane stack = new StackPane();
		
		stack.setStyle("-fx-border-color: #336699;");
		txtArea  = new TextArea();
		txtArea.setMaxHeight(200);
		txtArea.prefHeightProperty().bind(stack.prefWidthProperty());
		stack.getChildren().add(txtArea);
		return stack;
	}
	
	
	private HBox addTopPane() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 15, 15, 15));
		hbox.setSpacing(10); // Gap between nodes
		//hbox.setStyle("-fx-background-color: #336699;");
		hbox.setStyle("-fx-border-color: Blue;");
		

		buttons = new Button[6];
		for (int i = 0; i < buttons.length; i++) {

			buttons[i] = new Button(btntext[i]);
			buttons[i].setPrefSize(80, 20);
			buttons[i].prefHeightProperty().bind(hbox.prefWidthProperty());
		}

		for ( int i = 0; i < buttons.length; i++) {
			final int j = i;		 
			buttons[j].setOnAction( (e) -> {
				String str = buttons[j].getText();
				if ("clear".equals(str)) { clearTextFields(); }
				else if ("save".equals(str)) { 
					// addFundingIS();
					addTableRS();
					//addFundingSP();
				}
				else if ("update".equals(str)) { 
					// updateFundingSP();
					updateTableRS();
				}
				else if ("delete".equals(str)) { 
	//				deleteFundingDS();
	//				deleteFundingSP();
					deleteTableRS();
				}
				else if ("print".equals(str)) { printTable(); }
				else if ("search".equals(str)) { 
					
					String urlLink = "https://www.w3schools.com/sql/default.asp";
					try{
						Desktop.getDesktop().browse(new URI(urlLink));
					}
					catch(IOException ioException) {
						ioException.printStackTrace();
					}
					catch(URISyntaxException uriSyntaxException) {
						uriSyntaxException.printStackTrace();
					}
					finally { }
				}
				else 
					System.out.println("Not an apperiate button...");
			} );
		}
		
		
		hbox.getChildren().addAll(buttons);

		return hbox;
	}

	
// Top part
private  TreeView<String> addNodestoTree() {
    	TreeView<String> tree = new TreeView<String>();
    	
    	TreeItem<String> root, tables, reports, exit, about;
    	
    		root = new TreeItem<String>("RewardCrowdFunding");
    		
    		tables = new TreeItem<String>("Tables");
    		
    		makeChild(Nodes.Channel.toString(), tables);
    		makeChild(Nodes.donate.toString(), tables);
    		makeChild(Nodes.Funding.toString(), tables);
    		makeChild(Nodes.Member.toString(), tables);
    		makeChild(Nodes.LogTable.toString(), tables);
    		makeChild(Nodes.Plan_.toString(), tables);
    		makeChild(Nodes.Project.toString(), tables);
    		makeChild(Nodes.Subscribe.toString(), tables);
     		
    		reports = new TreeItem<String>("Reports");
    		makeChild(Nodes.Report01.toString(), reports);
    		makeChild(Nodes.Report02.toString(), reports);
    		makeChild(Nodes.Report03.toString(), reports);
    		
    		exit = new TreeItem<String>(Nodes.Exit.toString());
    		about=  new TreeItem<String>(Nodes.About.toString());
     		root.getChildren().addAll(tables,reports, exit, about);
    	    tree.setRoot(root);
         return tree;

    }	
    
	// Create child
	private TreeItem<String> makeChild(String title, TreeItem<String> parent) {
		TreeItem<String> item = new TreeItem<>(title);
		item.setExpanded(false);
		parent.getChildren().add(item);
		return item;
	}


	@Override
	public void start(Stage stage) {

		// Add controls and Layouts
		
		VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setMinSize(900, 500);
		vbox.setMaxSize(1200, 700);
		vbox.setPadding(new Insets(15, 15, 15, 15));
		vbox.setSpacing(10); // Gap between nodes
		vbox.setStyle("-fx-border-color: Black;");
		// Top Box
		HBox tbox=addTopPane();
		tbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(tbox);
		
		// Center box
		HBox cbox=addCenterPane();
		cbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(cbox);
			
		StackPane bbox=addBottomPane();
		bbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		// bbox.prefHeightProperty(); //
		vbox.getChildren().add(bbox);

		//create and show stage 

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.setTitle("RewardCrowdFunding");
		stage.show();
	}
	private void rsToTableView(String s) {

		table.getColumns().clear();
		for (int i = 0; i < table.getItems().size(); i++) {
			table.getItems().clear();
		}

		ObservableList data = FXCollections.observableArrayList();
		try {
			String query = "select * from " + s + "";
			PreparedStatement pst = null;
			pst = con.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();

			int colCount = rsmd.getColumnCount();

			// get data rows

			for (int i = 0; i < colCount; i++) {
 
				int dataType = rsmd.getColumnType(i + 1);

				final int j = i;
				TableColumn col = new TableColumn(rsmd.getColumnName(i + 1));

				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				table.getColumns().addAll(col);
			}
			while (rs.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int k = 1; k <= colCount; k++) {
					String str1 = rs.getString(k);
					if (str1 == null)
						str1 = "null";
					row.add(str1);
				}
				data.add(row);
			}

			table.setItems(data);

			table.getSelectionModel().select(0);
			showFields(); // 주석 달으라고 함

		} catch (Exception e) {
			txtArea.appendText(e.getMessage());
		} finally {
		}
	}

	private void showFields() {
		clearFields();
		TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
		System.out.println(table.getSelectionModel().getSelectedItem());
		int row = pos.getRow();
		int cols = table.getColumns().size();

		for (int j = 0; j < cols; j++) {
			Object ch = ((TableColumnBase) table.getColumns().get(j)).getText();
			Object cell = ((TableColumnBase) table.getColumns().get(j)).getCellData(row).toString();

			if (cell == null) {
				txt[j].setText("");
			} else {
				txt[j].setText(cell.toString());
				txt[j].setVisible(true);
			}
			labels[j].setText(ch.toString());
			labels[j].setVisible(true);
		}
	}
	
	
	private void clearFields() {
		for (int i = 0; i < txt.length; i++) {
			txt[i].setText("");
			txt[i].setVisible(false);
			labels[i].setText("");
			labels[i].setVisible(false);
		}
	}
	
	private void clearTextFields() {
		int noc = table.getColumns().size();
		for (int i = 0; i < noc; i++) {
			txt[i].setText(" ");
		}
	}
	
	// select Node
	private String mySelectedNode() {
		TreeItem ti = tree.getSelectionModel().selectedItemProperty().get();
		return ti.getValue().toString();
	}
	
	// insert Record using insert into statement
	private void addFundingIS() { 
		int num = 0;
		String sql = "insert into Funding values(?,?,?,?)";			// 4 values Parameter (1, 2, 3, 4)
		try { 
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, txt[0].getText());			// 1
			st.setString(2, txt[1].getText());			// 2
			st.setString(3, txt[2].getText());			// 3
			st.setString(4, txt[3].getText());			// 4
			
			num = st.executeUpdate();
			System.out.println(num + "record(s) are added");
			
			st.close();
		}
		catch(Exception e) { 	System.out.println(e.getMessage());		}
		finally { }
	}
	
	// insert Record using Resultset function
	private void addTableRS() { 
		String tn = mySelectedNode(); // table name
		String sql = "Select * from " + tn + "";
		try { 
			PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery();	// Reading Data
			
			rs.moveToInsertRow();				// Search Row

			
			int i = 0;
			if(tn == "donate") i = 1;
			while(txt[i].getLength() > 0) {
				rs.updateString(i+1, txt[i].getText());
				i++;
			}
			//rs.updateString(1, txt[0].getText());
			//rs.updateString(2, txt[1].getText());
			//rs.updateString(3, txt[2].getText());
			//rs.updateString(4, txt[3].getText());
			rs.insertRow();
			System.out.println(tn + "records are added");
			rs.close();
			st.close();
		}
		catch(Exception e) { 	System.out.println(e.getMessage());		}
		finally { }
	}
		
	// insert Record using Store Procedure
	private void addFundingSP() { 
		
		// String sql = "call sp_insertFunding(?,?,?,?)";
		String sql = "{ call sp_insertFunding(?,?,?,?) }";
	
		try {
			CallableStatement cst = con.prepareCall(sql);	// Callable STatement 
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.execute();
			System.out.println("records are added");
			cst.close();
		}
		catch(Exception e) { 	System.out.println(e.getMessage());		}
		finally { }
	}
	
	
	// delete Record using delete Statement Procedure
	private void deleteFundingDS() {
		String sql="delete from Funding where CID=?";
		String id = txt[0].getText();
		try {
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, id);
			st.executeUpdate();
			txtArea.appendText("Selected record is delete...\n ");
			st.close();
		}		
		catch(Exception e) { txtArea.appendText(e.getMessage()); }
		finally {}
	}
		
	// delete Record using delete Result Function
	private void deleteTableRS() {
		try {
			String tn = mySelectedNode(); // table name
			String sql="select * from " + tn + "";
			PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery();
			
			TablePosition pos = (TablePosition)table.getSelectionModel().getSelectedCells().get(0);
			int rownum = pos.getRow();
			txtArea.appendText(rownum + " is selected .. \n");
			
			rs.absolute(rownum+1);		//rownum : absolute
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText(rownum + " is deleted .. \n");
			rs.close();
			st.close();
		}
		catch(Exception e) { txtArea.appendText(e.getMessage()); }
		finally {}
	}
	
	// delete Record using dekete Statement Procedure
	// 수정한 부분 : primary key 만 사용할 것
	private void deleteFundingSP() {
		try {
			String sql = "{call usp_deleteFunding(?,?)}"; // User define Statement Procedure
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.execute();

			txtArea.appendText("usp_deleteFunding(?) is deleted .. \n");
			cst.close();
		}
		catch(Exception e) { txtArea.appendText(e.getMessage()); }
		finally {}
	}
	
	// update Record using update 
	// Project 기준으로 작성되어 추후 수정 필요
	private void updateFundingUS() {
		try {
			String sql = "update project set PNmae=?, PField = ? PDetails = ? where PID";
			PreparedStatement st = con.prepareStatement(sql);

			st.setString(1, txt[1].getText());	// PName을 의미. 1번째 요소
			st.setString(2, txt[2].getText());	// PField를 의미. 2번째 요소
			st.setString(3, txt[3].getText());	// PDetails를 의미. 3번째 요소
			st.setString(4, txt[0].getText());	// PID를 의미. 4번째 요소
		
			st.executeUpdate();
			txtArea.appendText(" Record is updated .. \n");
			st.close();
		}
		catch(Exception e) { txtArea.appendText(e.getMessage());	}
		finally {}
		
	}
	private void updateTableRS() {
		try {
			String tn = mySelectedNode(); // table name
			String sql="select * from " + tn + "";
			PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery();
			
			TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum = pos.getRow();
			txtArea.appendText(rownum + " is selected .. \n");
			
			rs.absolute(rownum + 1);
			int i = 0;
			if(tn == "donate") i = 1;
			while(txt[i].getLength() > 0) {
				rs.updateString(i+1, txt[i].getText());
				i++;
			}
			rs.updateRow();
			rs.first();
			txtArea.appendText(rownum + " is updated .. \n");
			rs.close();
			st.close();
		}
		catch(Exception e) { txtArea.appendText(e.getMessage());	}
		finally {}
		
	}
	private void updateFundingSP() {
		try {
			String sql = "{call usp_update_Funding(?,?,?,?)}";		//4 values. Funding 기준 // 만약에 할 거면 예를 들어 ssms -> RewordCrowdFunding -> 프로그래밍 기능 -> 저장 프로시저 -> dbo.update.Donate_
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated ..\n");
			cst.close();
		}
		catch(Exception e) { txtArea.appendText(e.getMessage());	}
		finally {}
		
	}
	private void printTable() {
		String tn = mySelectedNode(); // table name
		String fileName="C:\\Users\\HP\\JaspersoftWorkspace\\MyReports\\" + tn + ".jrxml";
		try {
			JasperReport jr = JasperCompileManager.compileReport(fileName);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			JasperViewer.viewReport(jp, false);
		}
		catch(Exception e) {
			txtArea.appendText("There is a problem in printing report.." + "\n");
			txtArea.appendText(e.getMessage());
		}
		finally { }
	}
	public static void main(String[] args) {
		launch(args);
	}

}
