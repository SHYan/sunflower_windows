package com.floreantpos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.floreantpos.Messages;
import com.floreantpos.config.AppConfig;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.mybatis.IBatisFactory;
import com.floreantpos.ui.dialog.POSMessageDialog;

/**
 *
 * @author jgilson
 */
public class JBackupController {
	
	public String currentDatabase = AppConfig.getDatabaseName();
	public String defaultPassword = AppConfig.getDatabasePassword();
	public boolean checkFolder(String folderPath) {
		String dump = folderPath+"pg_dump";
        File sqlFile = new File(dump);
        return sqlFile.isFile();
	}
    public JBackupController() {
    	String postgresqlFolder = TerminalConfig.getSqlFolder();
    	if(postgresqlFolder==null || postgresqlFolder.equals("") || !checkFolder(postgresqlFolder)) {
    		String location = (String) IBatisFactory.selectOneSQL("Report.get_pg_path", null);
            postgresqlFolder = location.substring(0,  location.indexOf("data")).concat("bin"+ File.separator);
            
    		if (checkFolder(postgresqlFolder)) {
                TerminalConfig.setSqlFolder(postgresqlFolder);
            }else {
            	JFileChooser fileChooser = new JFileChooser();
            	fileChooser.setCurrentDirectory(new java.io.File("C:\\"));
            	fileChooser.setDialogTitle("Select Postgres bin Folder");
            	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	fileChooser.setAcceptAllFileFilterUsed(false);
            	int option = fileChooser.showSaveDialog(com.floreantpos.util.POSUtil.getBackOfficeWindow());
    			if (option != JFileChooser.APPROVE_OPTION) {
    				POSMessageDialog.showMessage(com.floreantpos.util.POSUtil.getFocusedWindow(), "You must select Postgresql installed bin folder!");
    				TerminalConfig.setSqlFolder("");
    			}else {
    				File file = fileChooser.getSelectedFile();
    				TerminalConfig.setSqlFolder(file.getAbsolutePath()+File.separator);
    			}
    			
            }                        
    	}
        
    }
    
    public boolean backupDatabase() {

    	File defaultFolder = new File(System.getProperty("user.home")
                + File.separator + "backup_" + currentDatabase);

        if (!defaultFolder.exists()) {
            File dir = defaultFolder;
            dir.mkdirs();
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = "backup_" + currentDatabase + "_" + sdf.format(new Date()) + ".sql";
        
    	JFileChooser fileChooser = getFileChooser(defaultFolder.getAbsolutePath()+File.separator+fileName);
		int option = fileChooser.showSaveDialog(com.floreantpos.util.POSUtil.getBackOfficeWindow());
		if (option != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File file = fileChooser.getSelectedFile();
		if (file.exists()) {
			option = JOptionPane.showConfirmDialog(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("DataExportAction.1") + file.getName() + "?", Messages.getString("DataExportAction.3"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return executeCommand("backup", currentDatabase, file.getAbsolutePath());
		
    }
    
    public boolean restoreDatabase(String toDatabase) {
    	JFileChooser fileChooser = getFileChooser(System.getProperty("user.home") + File.separator);
		int option = fileChooser.showSaveDialog(com.floreantpos.util.POSUtil.getBackOfficeWindow());
		if (option != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		File file = fileChooser.getSelectedFile();
		option = JOptionPane.showConfirmDialog(com.floreantpos.util.POSUtil.getFocusedWindow(), Messages.getString("BackupRestoreAction.5"), Messages.getString("DataExportAction.3"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			return false;
		}
		boolean ret = executeCommand("dropdb", toDatabase, file.getAbsolutePath());
		if(ret) ret = executeCommand("createdb", toDatabase, file.getAbsolutePath());
		if(ret) ret =  executeCommand("restore", toDatabase, file.getAbsolutePath());
		return ret;
    }

    
    public boolean executeCommand(String type, String database, String databaseFilePath) {
    	List<String> commands = getPgComands(type, database, databaseFilePath);
        if (!commands.isEmpty()) {
            try {
            	
            	Runtime r = Runtime.getRuntime();
            	ProcessBuilder pb = new ProcessBuilder(commands);
                r = Runtime.getRuntime();
                pb.environment().put("PGPASSWORD", defaultPassword);
                Process process = pb.start();
                String line = "";
                try (BufferedReader buf = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    line = buf.readLine();
                    while (line != null) {
                        System.err.println(line);
                        line = buf.readLine();
                    }
                }catch(Exception e1) {
                	System.out.println("Error : read buf error");
                }
                process.waitFor();
                process.destroy();
                System.out.println("===> Success on " + type + " process.");
                return true;
            } catch (IOException | InterruptedException ex) {
                System.out.println("Exception: " + ex);
            }
        } else {
            System.out.println("Error: Invalid params.");
        }
        return false;
    }

    private List<String> getPgComands(String type, String database, String databaseFilePath) {

        ArrayList<String> commands = new ArrayList<>();
        
        String postgresqlFolder = TerminalConfig.getSqlFolder();
        String dump = postgresqlFolder+"pg_dump";
        String restore = postgresqlFolder+"pg_restore";
        String createdb = postgresqlFolder+"createdb";
        String dropdb = postgresqlFolder+"dropdb";
        switch (type) {
            case "backup":
                commands.add(dump);
                commands.add("-h"); //database server host
                commands.add("localhost");
                commands.add("-p"); //database server port number
                commands.add("5432");
                commands.add("-U"); //connect as specified database user
                commands.add("postgres");
                commands.add("-F"); //output file format (custom, directory, tar, plain text (default))
                commands.add("c");
                commands.add("-b"); //include large objects in dump
                commands.add("-v"); //verbose mode
                commands.add("-f"); //output file or directory name
                commands.add(databaseFilePath);
                commands.add("-d"); //database name
                commands.add(database);
                break;
                
            case "dropdb":
            	commands.add(dropdb);
            	commands.add("-h");
                commands.add("localhost");
                commands.add("-p");
                commands.add("5432");
                commands.add("-U");
                commands.add("postgres");
                commands.add(database);
                break;   
                
            case "createdb":
            	commands.add(createdb);
            	commands.add("-h");
                commands.add("localhost");
                commands.add("-p");
                commands.add("5432");
                commands.add("-U");
                commands.add("postgres");
                commands.add(database);
                break;
             
            case "restore":
                commands.add(restore);
                commands.add("-h");
                commands.add("localhost");
                commands.add("-p");
                commands.add("5432");
                commands.add("-U");
                commands.add("postgres");
                commands.add("-d");
                commands.add(database);
                commands.add("-v");
                commands.add(databaseFilePath);
                break;
                
            default:
                return Collections.EMPTY_LIST;
        }
        return commands;
    }
    
    
    public static JFileChooser getFileChooser(String defaultFile) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setSelectedFile(new File(defaultFile)); //$NON-NLS-1$
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "SQL File"; //$NON-NLS-1$
			}

			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".sql")) { //$NON-NLS-1$
					return true;
				}

				return false;
			}
		});
		return fileChooser;
	}
    

}
