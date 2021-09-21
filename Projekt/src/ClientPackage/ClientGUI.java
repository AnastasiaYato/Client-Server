/**
 * @Author Anastazja Noemi Lisowska
 */

package ClientPackage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

import static javax.swing.JOptionPane.*;

class MainWindow extends javax.swing.JFrame {

    private Client client; //a client thread
    private ArrayList<String> clients; //client's copy of current online users
    private String nick; //name of our client
    private boolean connected; //a flag for checking if we successfully logged in
    // Variables declaration - do not modify
    private javax.swing.JFrame ItemBrowser;
    private javax.swing.JMenuItem licenseMenuButton;
    private javax.swing.JButton createDirButton;
    private javax.swing.JMenuItem disconnectMenuButton;
    private javax.swing.JMenuItem connectMenuButton;
    private javax.swing.JPanel filesPanel;
    private javax.swing.JSlider fontSliderForFiles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem setDownloadPathMenuButton;
    private javax.swing.JMenuItem helpButton;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton loginButton;
    private javax.swing.JTextField nickField;
    private javax.swing.JPanel pathPanel;
    private javax.swing.JTextField pathText;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem authorsMenuButton;
    private javax.swing.JButton uploadButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel usersPanel;
    private javax.swing.JList<String> usersList;
    private DefaultListModel<String> userlistModel;
    private javax.swing.JList<String> filesList;
    private DefaultListModel<String> filelistModel;
    private javax.swing.JFileChooser jFileChooser;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JCheckBox nightModeButton;
    private javax.swing.JCheckBox hideNSFButton;
    private java.awt.Color nightBackground;
    private java.awt.Color dayBackground;
    private java.awt.Color nightListBackground;
    private java.awt.Color dayBackgroundAnother;

    /**
     * Creates new form ClientPackage.MainWindow
     */
    public MainWindow() {
        connected = false;
        nick = "";
        clients = new ArrayList<>( );
        initComponents( );
    }

    private void initComponents() {

        ItemBrowser = new javax.swing.JFrame( "File Server" );
        usersPanel = new javax.swing.JPanel( );
        userlistModel = new DefaultListModel<>( );
        usersList = new javax.swing.JList<>(userlistModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        uploadButton = new javax.swing.JButton( );
        filesPanel = new javax.swing.JPanel( );
        filelistModel = new DefaultListModel<>( );
        filesList = new javax.swing.JList<>(filelistModel);
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pathPanel = new javax.swing.JPanel( );
        pathText = new javax.swing.JTextField( );
        jTextField1 = new javax.swing.JTextField( );
        fontSliderForFiles = new javax.swing.JSlider( );
        refreshButton = new javax.swing.JButton( );
        saveButton = new javax.swing.JButton( );
        createDirButton = new javax.swing.JButton( );
        deleteButton = new javax.swing.JButton( );
        jMenuBar1 = new javax.swing.JMenuBar( );
        jMenu1 = new javax.swing.JMenu( );
        disconnectMenuButton = new javax.swing.JMenuItem( );
        connectMenuButton = new javax.swing.JMenuItem( );
        jMenu2 = new javax.swing.JMenu( );
        authorsMenuButton = new javax.swing.JMenuItem( );
        licenseMenuButton = new javax.swing.JMenuItem( );
        jLabel1 = new javax.swing.JLabel( );
        nickField = new javax.swing.JTextField( );
        loginButton = new javax.swing.JButton( );
        jFileChooser = new JFileChooser( );
        jScrollPane = new JScrollPane(filesList);
        jMenu3 = new javax.swing.JMenu( );
        setDownloadPathMenuButton = new javax.swing.JMenuItem( );
        helpButton = new javax.swing.JMenuItem( );
        nightModeButton = new javax.swing.JCheckBox( );
        hideNSFButton = new javax.swing.JCheckBox( );
        nightBackground = new java.awt.Color(210, 210, 210);
        nightListBackground = new java.awt.Color(170, 170, 170);
        dayBackground = new java.awt.Color(255, 255, 255);
        dayBackgroundAnother = new java.awt.Color(238, 238, 238);

        filesList.addListSelectionListener(new ListSelectionListener( ) {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                selectedFileValueChanged(evt);
            }
        });

        ItemBrowser.setResizable(false);
        nickField.addKeyListener(new java.awt.event.KeyListener( ) { //it will listen (since it will gain focus) for enter key if we typed something in it
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressedAction(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        });
        filesList.addKeyListener(new java.awt.event.KeyListener( ) { //it will listen for keys when list with files is focused
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressedActionItemBrowser(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        });
        ItemBrowser.addWindowListener(new WindowAdapter( ) { //handles window closing, so if we're connected to the server it will first disconnect then exit
            public void windowClosing(WindowEvent we) {
                if (connected) {
                    client.disconnect( );
                }
                System.exit(0);

            }
        });

        usersPanel.setBackground(new java.awt.Color(255, 255, 255));
        javax.swing.GroupLayout usersPanelLayout = new javax.swing.GroupLayout(usersPanel);
        usersPanel.setLayout(usersPanelLayout);
        usersPanelLayout.setHorizontalGroup(
                usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(usersPanelLayout.createSequentialGroup( )
                                .addGap(0, 1, Short.MAX_VALUE)
                                .addComponent(usersList, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 1, Short.MAX_VALUE))
        );
        usersPanelLayout.setVerticalGroup(
                usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(usersPanelLayout.createSequentialGroup( )
                                .addGap(0, 1, Short.MAX_VALUE)
                                .addComponent(usersList, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 1, Short.MAX_VALUE))
        );

        uploadButton.setText("Upload file");
        uploadButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });
        nightModeButton.setText("NightMode");
        nightModeButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nightModeButtonActionPerformed(evt);
            }
        });
        hideNSFButton.setText("Hide NSF Files");
        hideNSFButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideNSFButtonActionPerformed(evt);
            }
        });
        hideNSFButton.setToolTipText("Hides Non Selectable Files - It will cause a Refresh of current view!");

        filesPanel.setBackground(new java.awt.Color(255, 255, 255));
        filesPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout filesPanelLayout = new javax.swing.GroupLayout(filesPanel);
        filesPanel.setLayout(filesPanelLayout);
        filesPanelLayout.setHorizontalGroup(
                filesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(filesPanelLayout.createSequentialGroup( ))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        filesPanelLayout.setVerticalGroup(
                filesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(filesPanelLayout.createSequentialGroup( ))
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1, Short.MAX_VALUE)
        );

        pathText.setEditable(false);

        javax.swing.GroupLayout pathPanelLayout = new javax.swing.GroupLayout(pathPanel);
        pathPanel.setLayout(pathPanelLayout);
        pathPanelLayout.setHorizontalGroup(
                pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pathText)
        );
        pathPanelLayout.setVerticalGroup(
                pathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pathText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Users online");

        fontSliderForFiles.setMaximum(15);
        fontSliderForFiles.setMinimum(12);
        fontSliderForFiles.setValue(12);
        fontSliderForFiles.addChangeListener(new javax.swing.event.ChangeListener( ) {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fontSliderForFilesStateChanged(evt);
            }
        });

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save file");
        saveButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        createDirButton.setText("Create directory");
        createDirButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createDirButtonActionPerformed(evt);
            }
        });
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("Program");
        jMenu1.add(connectMenuButton);
        connectMenuButton.setText("Connect");
        connectMenuButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectMenuButtonActionPerformed(evt);
            }
        });
        jMenu1.add(connectMenuButton);
        disconnectMenuButton.setText("Disconnect and Exit");
        disconnectMenuButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMenuButtonActionPerformed(evt);
            }
        });
        jMenu1.add(disconnectMenuButton);
        jMenuBar1.add(jMenu1);

        jMenu2.setText("About");

        authorsMenuButton.setText("Author");
        authorsMenuButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authorsMenuButtonActionPerformed(evt);
            }
        });
        jMenu2.add(authorsMenuButton);

        licenseMenuButton.setText("License");
        licenseMenuButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseMenuButtonActionPerformed(evt);
            }
        });
        jMenu2.add(licenseMenuButton);

        jMenuBar1.add(jMenu2);


        jMenu3.setText("Settings");

        setDownloadPathMenuButton.setText("Set Downloads Folder");
        setDownloadPathMenuButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPathForDownloadedFilesButtonActionPerformed(evt);
            }
        });
        setDownloadPathMenuButton.setToolTipText("Default saving folder is "+System.getProperty("user.dir"));
        jMenu3.add(setDownloadPathMenuButton);

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        jMenu3.add(hideNSFButton);
        jMenu3.add(nightModeButton);
        jMenu3.add(helpButton);
        jMenuBar1.add(jMenu3);


        ItemBrowser.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout ItemBrowserLayout = new javax.swing.GroupLayout(ItemBrowser.getContentPane( ));
        ItemBrowser.getContentPane( ).setLayout(ItemBrowserLayout);
        ItemBrowserLayout.setHorizontalGroup(
                ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                .addContainerGap( )
                                .addGroup(ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                                .addGroup(ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(filesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(pathPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                                        .addComponent(usersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addContainerGap( ))
                                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                                .addGap(13, 13, 13)
                                                .addGroup(ItemBrowserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                                                .addComponent(uploadButton)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(saveButton)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(createDirButton)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(deleteButton)
                                                                .addGap(30))
                                                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                                                .addComponent(refreshButton)
                                                                .addGap(50)
                                                                .addComponent(fontSliderForFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        )))
        );
        ItemBrowserLayout.setVerticalGroup(
                ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ItemBrowserLayout.createSequentialGroup( )
                                .addContainerGap( )
                                .addGroup(ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(pathPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(ItemBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(usersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(filesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(8, 8, 8)
                                .addGroup(ItemBrowserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(fontSliderForFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(refreshButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(ItemBrowserLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(uploadButton)
                                        .addComponent(saveButton)
                                        .addComponent(createDirButton)
                                        .addComponent(deleteButton))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.addWindowListener(new WindowAdapter( ) { //better handling of closing the app
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        jLabel1.setText("Please enter your name");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener( ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane( ));
        getContentPane( ).setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup( )
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(nickField))
                                .addGap(0, 35, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup( )
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(loginButton)
                                .addGap(63, 63, 63))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup( )
                                .addGap(8, 8, 8)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nickField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(loginButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.pack( );
        this.setResizable(false);
        this.setTitle("Login");
        this.setLocationRelativeTo(null);
    }// </editor-fold>

    /**
     * Shortcuts handling
     */
    private void keyPressedAction(KeyEvent e) { //we need different KeyPressedActions, because we don't use Enter key in ItemBrowser etc
        if (e.getKeyCode( ) == KeyEvent.VK_ENTER) {
            loginButton.doClick( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    private void keyPressedActionItemBrowser(KeyEvent e) {
        if (e.getKeyCode( ) == KeyEvent.VK_F1) {
            connectMenuButton.doClick( );
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_F2) {
            disconnectMenuButton.doClick( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_F5) {
            refreshButton.doClick( );
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_F7) {
            //--
            if (fontSliderForFiles.getValue( ) > 12)
                fontSliderForFiles.setValue(fontSliderForFiles.getValue( ) - 1);
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_F8) {
            //++
            if (fontSliderForFiles.getValue( ) < 15)
                fontSliderForFiles.setValue(fontSliderForFiles.getValue( ) + 1);
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_F12) {
            helpButton.doClick( );
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_U) {
            uploadButton.doClick( );
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_S) {
            if (!filesList.isSelectionEmpty( )) {
                saveButton.doClick( );
            }
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_C) {
            createDirButton.doClick( );
            filesList.requestFocus( );
        }
        if (e.getKeyCode( ) == KeyEvent.VK_D) {
            deleteButton.doClick( );
            filesList.requestFocus( );
        }
    }

    /**
     * Login button handling
     */
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if ((nickField.getText( ).length( ) < 3 || nickField.getText( ).contains(" "))) { //we can only type in a nick that does not contain spaces and nick that has more than 3 characters
            showMessageDialog(this, "Nick must be longer than 3 letters and cannot contain spaces", "Nick not valid", JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.setVisible(false); //hide the main frame
            nick = (nickField.getText( )); //set our nick
            ItemBrowser.pack( ); //pack second frame
            ItemBrowser.setLocationRelativeTo(null); //center it
            ItemBrowser.setVisible(true); //show it
            filesList.requestFocus( ); //request a focus for files list so we can use shortcuts
            client = new Client(nick, this);
            /**
             * Disables ItemBrowser buttons, they won't work unless we're connected to server
             */
            hideNSFButton.setEnabled(false);
            disconnectMenuButton.setEnabled(false);
            uploadButton.setEnabled(false);
            refreshButton.setEnabled(false);
            saveButton.setEnabled(false);
            createDirButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    /**
     * Changes the size of files in the viewed list
     */
    private void fontSliderForFilesStateChanged(javax.swing.event.ChangeEvent evt) { //handles changing a size of a font in list
        if (!fontSliderForFiles.getValueIsAdjusting( )) {
            int value = fontSliderForFiles.getValue( );
            filesList.setFont(new Font("Dialog", Font.BOLD, value));
        }
    }

    /**
     * Control buttons handling
     */
    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        client.sendCommand("RefreshFiles");
    }

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles uploading a file
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); //we can only chose files to send
        int returnVal = jFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile( );
            if(file.getName().startsWith("."))
            {
                showMessageDialog(this, "You cannot upload NSF files!", "Warning", WARNING_MESSAGE);
            }
            else{
                System.out.println("Sending: " + file.getName( ));
                try {
                    client.uploadFile(file);
                } catch (Exception e) {
                    e.printStackTrace( );
                }
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles saving (downloading of) a file
        String fileToDownload = filesList.getSelectedValue( );
        if(filesList.getSelectedValue( ).startsWith("NSF") ) {
            showMessageDialog(this, "You cannot download NSF files!", "Warning", WARNING_MESSAGE);
        }
        else {
            if (!fileToDownload.equals("...") ||  filesList.getSelectedValue( ).contains(".")) {
                client.sendCommand("Download");
                client.sendCommand(filesList.getSelectedValue( ));
            }
        }
    }

    private void createDirButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles creating of a directory
        String folderName = showInputDialog(this, "Name your folder", ""); //getting a name of our directory
        if (folderName.startsWith("."))
        {
            showMessageDialog(this, "You cannot create NSF files!", "Warning", WARNING_MESSAGE);
        }
        else {
            client.sendCommand("CreateDir");
            client.sendCommand(folderName);
        }

    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles deleting of a file or a directory
        if (!filesList.isSelectionEmpty( )) {//if selection is not empty it means that we have something selected - delete this item
            if (!filesList.getSelectedValue( ).startsWith("NSF")) {
                {
                    int result = showConfirmDialog(this, "Do you want to Delete this File?", "File Removal", YES_NO_OPTION);
                    if (result == YES_OPTION) {
                        client.sendCommand("Remove");
                        client.sendCommand("File");
                        client.sendCommand(filesList.getSelectedValue( ));
                    }
                }
            } else {
                showMessageDialog(this, "You cannot delete NSF files!", "Warning", WARNING_MESSAGE);
            }

        } else { //else selection is empty, so delete the folder we're in
            int result = showConfirmDialog(this, "Do you want to Delete this Folder?", "Directory Removal", YES_NO_OPTION);
            if (result == YES_OPTION) {
                client.sendCommand("Remove");
                client.sendCommand("Directory");
                client.sendCommand("");
                client.sendCommand("GoBack");
            }
        }
    }

    private void connectMenuButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles connecting to the server via button
        client.start( ); //starts client thread so it won't get stuck with swing thread
        connected = true; //sets connected flag to true
        connectMenuButton.setEnabled(false); //disable connection button as it's not longer needed and it would cause unnecessary exceptions
        /**
         * Enables ItemBrowser buttons
         */
        hideNSFButton.setEnabled(true);
        disconnectMenuButton.setEnabled(true);
        uploadButton.setEnabled(true);
        refreshButton.setEnabled(true);
        saveButton.setEnabled(true);
        createDirButton.setEnabled(true);
        deleteButton.setEnabled(true);
        setDownloadPathMenuButton.setEnabled(true);
    }

    private void disconnectMenuButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles disconnecting with the server via button
        client.disconnect( );
    }

    private void authorsMenuButtonActionPerformed(java.awt.event.ActionEvent evt) { //shows Author nuff said
        showMessageDialog(this, "Anastazja Noemi Lisowska", "Author", JOptionPane.INFORMATION_MESSAGE);
    }

    private void licenseMenuButtonActionPerformed(java.awt.event.ActionEvent evt) { //license
        showMessageDialog(this, "Creative Commons BY-NC-SA", "Author", JOptionPane.INFORMATION_MESSAGE);
    }

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) { //shows shortcuts list
        showMessageDialog(this,
                """
                        Shortcuts
                        F1 - Connect
                        F2 - Disconnect
                        F5 - Refresh
                        F7-F8 - Zoom in/out file list
                        F12 - Help
                        S/U/C/D - Save/Upload/Create/Delete""",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setPathForDownloadedFilesButtonActionPerformed(java.awt.event.ActionEvent evt) { //handles setting a download path
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //we want to pick directories only
        int returnVal = jFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile( );
            System.out.println("Setting a default Download Folder: " + Path.of(file.getAbsolutePath( )));
            client.setDownloadsFolder(Path.of(file.getAbsolutePath( )));
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }
    private void hideNSFButtonActionPerformed(java.awt.event.ActionEvent evt) {
        client.sendCommand("RefreshFiles");
    }
    private void nightModeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(nightModeButton.isSelected()){
            filesList.setBackground(nightListBackground);
            usersList.setBackground(nightListBackground);
            ItemBrowser.getContentPane().setBackground(nightBackground);
            fontSliderForFiles.setBackground(nightBackground);
        }
        else {
            filesList.setBackground(dayBackground);
            usersList.setBackground(dayBackground);
            ItemBrowser.getContentPane().setBackground(dayBackgroundAnother);
            fontSliderForFiles.setBackground(dayBackgroundAnother);
        }
    }

    private void selectedFileValueChanged(ListSelectionEvent evt) { //handles list selection of files or directories every time we select something from a list
        if (!filesList.isSelectionEmpty( )) {
            /**
             * Selection handling based on what we clicked on
             */
            if (filesList.getSelectedValue( ).equals("...")) { //for going back
                client.sendCommand("GoBack");
            } else if (filesList.getSelectedValue( ).startsWith("NSF")) { //NSF (Non Selectable File) files are ones we won't temper with
                System.out.println("Not available");
            } else if (filesList.getSelectedValue( ).contains(".")) {
                System.out.println("A file");
            } else {
                String selectedFile = filesList.getSelectedValue( );
                if (selectedFile.contains("Folder: "))
                    selectedFile = filesList.getSelectedValue( ).replaceAll("Folder: ", "");
                System.out.println("A directory");
                client.sendCommand("GoTo");
                client.sendCommand(selectedFile);
            }
        }
    }

    public void updateOnlineUsers(ArrayList<String> users) { //handles updating users list
        userlistModel.removeAllElements( );
        for (String user : users) {
            userlistModel.addElement(user);
        }
    }

    public void updateFiles(String path, ArrayList<String> files) { //handles file view list
        pathText.setText(path);
        filelistModel.removeAllElements( );
        Path filesPath = Path.of(path);
        if (!(filesPath.getParent( ) == null)) //if we're able to go back one directory backwards add ... object that allows us to go back
            filelistModel.addElement("...");
        for (String file : files) {
            if (file.contains(".") && !file.startsWith("."))
                filelistModel.addElement(file);
            else if (file.startsWith(".") )
            {
                if(!hideNSFButton.isSelected())
                filelistModel.addElement("NSF: " + file);// Non Selectable Files
            }

            else
                filelistModel.addElement("Folder: " + file);
        }
    }
    // End of variables declaration
}


public class ClientGUI {
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow( );
        mainWindow.setVisible(true);
    }
}