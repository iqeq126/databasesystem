
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSetMetaData;
import com.sun.prism.paint.Color;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MetaData {
	
	private Connection con = MyConnection.makeConnection();
	private TreeView<String> tree;
	private Label[] labels;
	private TextField[] txt;

	public void getStage() {
		Stage stage = new Stage();
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		vbox.setMinSize(800, 600);
		vbox.setMaxSize(1000, 800);
		vbox.setPadding(new Insets(15, 15, 15, 15));

		// add Top Pane
		BorderPane tbox = addTopPane();
		tbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(tbox);

		// add Center box
		HBox cbox = addCenterPane();
		cbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(cbox);

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.setTitle("Database Meta Data");
		stage.show();
	}

	private BorderPane addTopPane() {
		BorderPane bPane = new BorderPane();

		bPane.setPadding(new Insets(15, 15, 50, 15));
		Text text = new Text("About my Database");
		text.setFont(Font.font("Arial", FontWeight.BOLD, 36));

		bPane.setCenter(text);

		return bPane;
	}

	private HBox addCenterPane() {

		HBox hb1 = new HBox();

		VBox vb = new VBox();

		// Add Labels and TextFields
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(50, 15, 15, 20));
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setStyle("-fx-border-color: Blue;");
		// gp.prefHeightProperty().bind(tree.prefHeightProperty());
		txt = new TextField[10];
		labels = new Label[10];

		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label("Label..");
			labels[i].setFont(Font.font("Arial", FontWeight.BOLD, 14));
			labels[i].setMinSize(150, 40);
			txt[i] = new TextField(" Text.. ");
			txt[i].setMinSize(300, 40);
			txt[i].setEditable(false);
			txt[i].setFont(Font.font("Arial", FontWeight.BOLD, 14));
			gp.addRow(i, labels[i], txt[i]);
			labels[i].prefHeightProperty().bind(gp.widthProperty());
			txt[i].prefHeightProperty().bind(gp.widthProperty());
		}

		// you will write this function
		fillGrid();

		vb.getChildren().addAll(gp);

		// Add TreeView
		StackPane stack = new StackPane();

		// Create the tree
		tree = addNodestoTree(); // you will write
		tree.setShowRoot(true);

		tree.setMaxWidth(400);
		tree.prefWidthProperty().bind(stack.prefWidthProperty());
		stack.getChildren().add(tree);

		hb1.getChildren().addAll(stack, vb);
		hb1.setStyle("-fx-border-color:black;");
		hb1.setSpacing(20);
		hb1.prefHeightProperty().bind(vb.prefWidthProperty());

		return hb1;
	}

	private TreeView<String> addNodestoTree() {

		TreeView<String> tree = new TreeView<String>();
		TreeItem<String> root, tables, sfunction, tfunctions, sprocedures, triggers;

		root = new TreeItem<String>("RewardCrowdFunding");
		tree.setRoot(root);

		tables = new TreeItem<String>(Nodes02.Tables.toString());

		
		try {
			String sql1 = "select object_name as tn from getListTables()";
			ResultSet rs1 = (con.prepareStatement(sql1)).executeQuery();
			
			int i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, tables);

				String sql2 = "select * from getColumnsList(?)";
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {
					String cnames = rs2.getString("column_name") + "(" + rs2.getString("column_type") + ","
							+ rs2.getString("max_length") + ")";

					makeChild(cnames, tables.getChildren().get(i));

				}
				i++;
				rs2.close();
			}
			rs1.close();
			

			sfunction = new TreeItem<String>(Nodes02.Scalar_Functions.toString());
			// add Scalar valued function and their parameters
			
			sql1 = "select SPECIFIC_NAME as tn from INFORMATION_SCHEMA.ROUTINES where DATA_TYPE != 'TABLE'";
			rs1 = (con.prepareStatement(sql1)).executeQuery();
			
			i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, sfunction);

				String sql2 = "select * from task7(?)";
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {
					String cnames = rs2.getString("parameter_name") + "(" + rs2.getString("parameter_type") + ","
							+ rs2.getString("max_length") + ")";

					makeChild(cnames, sfunction.getChildren().get(i));
				}
				
				i++;
				rs2.close();
			}
			rs1.close();
			
			tfunctions = new TreeItem<String>(Nodes02.Table_Functions.toString());
			// add Table valued function and their parameters
			sql1 = "select SPECIFIC_NAME as tn from INFORMATION_SCHEMA.ROUTINES where DATA_TYPE = 'TABLE'";
			rs1 = (con.prepareStatement(sql1)).executeQuery();
			
			i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, tfunctions );

				String sql2 = "select * from getParameters(?)";
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {
					String cnames = rs2.getString("parameter_name") + "(" + rs2.getString("parameter_type") + ","
							+ rs2.getString("max_length") + ")";

					makeChild(cnames, tfunctions.getChildren().get(i));
				}
				
				i++;
				rs2.close();
			}
			rs1.close();
			
			sprocedures = new TreeItem<String>(Nodes02.Stored_Procedures.toString());
			// add Stored procedures and their parameters
			sql1 = "select SPECIFIC_NAME as tn from INFORMATION_SCHEMA.ROUTINES where ROUTINE_TYPE = 'PROCEDURE'";
			rs1 = (con.prepareStatement(sql1)).executeQuery();
			
			i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, sprocedures);

				String sql2 = "select * from task7(?)";
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {
					String cnames = rs2.getString("parameter_name") + "(" + rs2.getString("parameter_type") + ","
							+ rs2.getString("max_length") + ")";

					makeChild(cnames, sprocedures.getChildren().get(i));
				}
				
				i++;
				rs2.close();
			}
			rs1.close();
			
			triggers = new TreeItem<String>(Nodes02.Triggers.toString());
			// add Trigger
			sql1 = "select name as tn from sys.triggers";
			rs1 = (con.prepareStatement(sql1)).executeQuery();
			
			i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, triggers);

				String sql2 = "select * from sys.triggers";
				PreparedStatement pst = con.prepareStatement(sql2);
				//pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {
					String cnames = "(" + rs2.getString("create_date") + ","
							+ rs2.getString("modify_date") + ")";

					makeChild(cnames, triggers.getChildren().get(i));
				}
				
				i++;
				rs2.close();
			}
			rs1.close();
			
			root.getChildren().addAll(tables, sfunction, tfunctions, sprocedures, triggers);

		} 
		
		catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		finally {
		}

		return tree;
	}

	// Create child
	private TreeItem<String> makeChild(String title, TreeItem<String> parent) {
		TreeItem<String> item = new TreeItem<>(title);
		item.setExpanded(false);
		parent.getChildren().add(item);
		return item;
	}

	private void fillGrid() {
		try {
			DatabaseMetaData dm = (DatabaseMetaData) con.getMetaData();
			// Point
			labels[0].setText("Database name");
			String sql0 = "select db_name() as dn";
			PreparedStatement ps0 = con.prepareStatement(sql0);
			ResultSet rs0 = ps0.executeQuery();
			rs0.next();
			txt[0].setText(rs0.getString("dn"));

			// txt[0].setText(con.getCatalog());

			labels[1].setText("Number of Tables ");
			String sql = "select count(*) as c from getListTables()";
			//String sql = "select count(*) as c from information_schema.tables";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			//ResultSet rs = (con.prepareStatement("select dbo.numTables() as nt")).executeQuery();
			rs.next();
			txt[1].setText(rs.getString(1));
			rs.close();

			labels[2].setText("Number of Scalar Valued Functions ");
			sql = "select count(*) from INFORMATION_SCHEMA.ROUTINES where DATA_TYPE != 'table'";
			ps =  con.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			txt[2].setText(rs.getString(1));
			rs.close();
			
			labels[3].setText("Number of Table-Valued Functions");
			sql = "select count(*) from INFORMATION_SCHEMA.ROUTINES where DATA_TYPE = 'table'";
			ps =  con.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			txt[3].setText(rs.getString(1));
			rs.close();
			
			labels[4].setText("Number of Stored procedures ");
			sql = "select count(*) from INFORMATION_SCHEMA.ROUTINES where ROUTINE_TYPE = 'PROCEDURE'";
			ps =  con.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			txt[4].setText(rs.getString(1));
			rs.close();

			labels[5].setText("Number of Triggers ");
			sql = "select count(*) from sys.triggers";
			ps =  con.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			txt[5].setText(rs.getString(1));
			rs.close();
			
			labels[6].setText("DBMS name: ");
			txt[6].setText(dm.getDatabaseProductName());

			labels[7].setText("DBMS Version: ");
			txt[7].setText(dm.getDatabaseProductVersion());

			labels[8].setText("JDBC Driver name: ");
			txt[8].setText(dm.getDriverName());

			labels[9].setText("JDBC Driver Version: ");
			txt[9].setText(dm.getDriverVersion());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
		}

	}

}