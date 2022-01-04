import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.undo.*;
class UndoManagerHelper
{
	  public static Action getUndoAction(UndoManager manager, String label)
	  {
	    return new UndoAction(manager, label);
	  }

	  public static Action getUndoAction(UndoManager manager)
	  {
	    return new UndoAction(manager, (String) UIManager.get("AbstractUndoableEdit.undoText"));
	  }

	  public static Action getRedoAction(UndoManager manager, String label)
	  {
	    return new RedoAction(manager, label);
	  }

	  public static Action getRedoAction(UndoManager manager)
	  {
	    return new RedoAction(manager, (String) UIManager.get("AbstractUndoableEdit.redoText"));
	  }

	  private abstract static class UndoRedoAction extends AbstractAction
	  {
		  UndoManager undoManager = new UndoManager();
		  String errorMessage = "Cannot Undo";
		  String errorTitle = "Undo Problem";
		  protected UndoRedoAction(UndoManager manager, String name)
		  {
			  super(name);
			  undoManager = manager;
		  }
		  public void setErrorMessage(String newValue)
		  {
			  errorMessage = newValue;
		  }
		  public void setErrorTitle(String newValue)
		  {
			  errorTitle = newValue;
		  }
		  protected void showMessage(Object source)
		  {
			  if (source instanceof Component)
			  {
				  JOptionPane.showMessageDialog((Component) source, errorMessage, errorTitle,
				  JOptionPane.WARNING_MESSAGE);
			  }
			  else
			  {
				  System.err.println(errorMessage);
			  }
		  }
	  }
	  public static class UndoAction extends UndoRedoAction
	  {
		    public UndoAction(UndoManager manager, String name)
		    {
		        super(manager, name);
		        setErrorMessage("Cannot Undo!!!");
		        setErrorTitle("Undo Problem");
		    }
     	    public void actionPerformed(ActionEvent actionEvent)
     	    {
		        try
		        {
		          undoManager.undo();
		        }
		        catch (CannotUndoException cannotUndoException)
		        {
		          showMessage(actionEvent.getSource());
		        }
		    }
	   }
	   public static class RedoAction extends UndoRedoAction
	   {
		      public RedoAction(UndoManager manager, String name)
		      {
		          super(manager, name);
		          setErrorMessage("Cannot Redo!!!");
		          setErrorTitle("Redo Problem");
		      }
		      public void actionPerformed(ActionEvent actionEvent)
		      {
		         try
		         {
		             undoManager.redo();
		         }catch (CannotRedoException cannotRedoException)
		         {
		             showMessage(actionEvent.getSource());
		         }
		      }
		}
}

class NotepadProject extends JFrame implements ActionListener
{
	    static int fsize=17;

	    JTextArea t = new JTextArea();
	    JScrollPane jp = new JScrollPane(t);
	    JFrame f;		// Create a frame

	    JList familylist, stylelist, sizelist;
	    String familyvalue[]={"Times New Roman","Courier New","Courier","Georgia","Bookman","Comic Sans MS","Agency FB","Antiqua","Architect","Arial","Calibri","Comic Sans","Courier","Cursive","Impact","Serif"};
		String sizevalue[]={"5","10","15","20","25","30","35","40","45","50","55","60","65","70"};
		int [] stylevalue={ Font.PLAIN, Font.BOLD, Font.ITALIC };
		String [] stylevalues={ "PLAIN", "BOLD", "ITALIC" };
		String ffamily, fsizestr, fstylestr;
		int fstyle;
		Font font1;

	    JMenuBar mb = new JMenuBar();		// Create a Menu Bar

		JMenu file = new JMenu("File");		// Create 1st menu to Add in Menu Bar
		JMenu edit = new JMenu("Edit");		// Create 2nd menu to Add in Menu Bar
		JMenu compile = new JMenu("Compile and Run");		// Create 3rd menu to Add in Menu Bar
		JMenu format = new JMenu("Format");		// Create 4th menu to Add in Menu Bar
		JMenu help = new JMenu("Help");		// Create 5th menu to Add in Menu Bar

		UndoManager manager = new UndoManager();

		JMenuItem n = new JMenuItem("New");		// Adding Menu Items in Menu Bar
	    JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem close = new JMenuItem("Close");
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem print = new JMenuItem("Print");
		JMenuItem selectall = new JMenuItem("Select All");
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem paste = new JMenuItem("Paste");
		JMenuItem undo = new JMenuItem(UndoManagerHelper.getUndoAction(manager,"Undo"));
        JMenuItem redo = new JMenuItem(UndoManagerHelper.getRedoAction(manager,"Redo"));
		JMenuItem java = new JMenuItem("Java");
		JMenuItem python = new JMenuItem("Python");
		JMenuItem fontfamily = new JMenuItem("Font Family");
        JMenuItem fontstyle = new JMenuItem("Font Style");
        JMenuItem fontsize = new JMenuItem("Font Size");
		JMenuItem about = new JMenuItem("About");
		JMenuItem color = new JMenuItem("Choose Color");

	NotepadProject()		 //NotepadProject named constructor
	{
		setSize(800,500);
		setTitle("Text Editor");

		n.setMnemonic('N');

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_N , InputEvent.CTRL_MASK);
			n.setAccelerator(stroke);
		KeyStroke st =KeyStroke.getKeyStroke(KeyEvent.VK_O , InputEvent.CTRL_MASK);
			open.setAccelerator(st);
		KeyStroke cu = KeyStroke.getKeyStroke(KeyEvent.VK_X , InputEvent.CTRL_MASK);
			cut.setAccelerator(cu);
		KeyStroke cpy = KeyStroke.getKeyStroke(KeyEvent.VK_C , InputEvent.CTRL_MASK);
			copy.setAccelerator(cpy);
		KeyStroke pst = KeyStroke.getKeyStroke(KeyEvent.VK_V , InputEvent.CTRL_MASK);
			paste.setAccelerator(pst);
		KeyStroke sta = KeyStroke.getKeyStroke(KeyEvent.VK_A , InputEvent.CTRL_MASK);
			selectall.setAccelerator(sta);
		KeyStroke sav = KeyStroke.getKeyStroke(KeyEvent.VK_S , InputEvent.CTRL_MASK);
			save.setAccelerator(sav);
		KeyStroke prt = KeyStroke.getKeyStroke(KeyEvent.VK_P , InputEvent.CTRL_MASK);
			print.setAccelerator(prt);
		KeyStroke und = KeyStroke.getKeyStroke(KeyEvent.VK_Z , InputEvent.CTRL_MASK);
			undo.setAccelerator(und);
		KeyStroke red = KeyStroke.getKeyStroke(KeyEvent.VK_Y , InputEvent.CTRL_MASK);
			redo.setAccelerator(red);

		font1=new Font("Arial",Font.PLAIN,17);
		familylist = new JList(familyvalue);
		stylelist = new JList(stylevalues);
		sizelist = new JList(sizevalue);

		familylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sizelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stylelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		file.add(n);
		file.add(open);
		file.add(save);
		file.addSeparator();
		file.add(print);
		file.addSeparator();
		file.add(close);

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(selectall);
		edit.addSeparator();
		edit.addSeparator();
		edit.add(undo);
		edit.addSeparator();
		edit.add(redo);

		format.add(fontfamily);
		format.add(fontsize);
		format.add(fontstyle);
		format.add(color);

		compile.add(java);
		compile.addSeparator();
		compile.add(python);

		help.add(about);


		mb.add(file);
		mb.add(edit);
		mb.add(format);
		mb.add(compile);
		mb.add(help);

		setJMenuBar(mb);

		add(jp);

		//Adding Action Listener to Menu
		n.addActionListener(this);
		open.addActionListener(this);
		close.addActionListener(this);
		save.addActionListener(this);
		cut.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
		print.addActionListener(this);
		selectall.addActionListener(this);
		undo.addActionListener(this);
		redo.addActionListener(this);
		about.addActionListener(this);
		java.addActionListener(this);
		python.addActionListener(this);
		fontstyle.addActionListener(this);
		fontsize.addActionListener(this);
		fontfamily.addActionListener(this);
		color.addActionListener(this);


		t.getDocument().addUndoableEditListener(manager);
		t.setFont(new Font("Times new Roman", Font.BOLD, 20));
        t.setBackground(Color.WHITE);
        t.setForeground(Color.BLUE);

	}

		//If a button is Pressed
		public void actionPerformed(ActionEvent e)
		{
			String s = e.getActionCommand();

			if (s.equals("Cut")) {
						t.cut();
					}
			else if (s.equals("Copy")) {
						t.copy();
					}
			else if (s.equals("Paste")) {
						t.paste();
					}
			else if (s.equals("Save")) {
						//Create an object of JFileChooser class
						JFileChooser j = new JFileChooser("c:");

						// Invoke the showsSaveDialog function to show the save dialog
						int r = j.showSaveDialog(null);

						if (r == JFileChooser.APPROVE_OPTION)
						{
							// Set the label to the path of the selected directory
							File fi = new File(j.getSelectedFile().getAbsolutePath());

							try {
								FileWriter wr = new FileWriter(fi, false);		// Create a file writer

								// Create buffered writer to write
								BufferedWriter w = new BufferedWriter(wr);

								// Write
								w.write(t.getText());
								w.flush();
								w.close();
							}
							catch (Exception evt) {
								JOptionPane.showMessageDialog(f, evt.getMessage());
							}
						}
						// If the user cancelled the operation
						else
							JOptionPane.showMessageDialog(f, "Operation Cancelled!!!");
					}
			else if (s.equals("Print")) {
						try {
							t.print();
						}
						catch (Exception evt) {
							JOptionPane.showMessageDialog(f, evt.getMessage());
						}
					}
			else if (s.equals("Open"))
			{
						// Create an object of JFileChooser class
						JFileChooser j = new JFileChooser("c:");

						// Invoke the showsOpenDialog function to show the save dialog
						int r = j.showOpenDialog(null);

						 // If the user selects a file
						if (r == JFileChooser.APPROVE_OPTION)
						{
							// Set the label to the path of the selected directory
							File fi = new File(j.getSelectedFile().getAbsolutePath());

							try {

								// String
								String s1 = "", sl = "";

      							// File reader
								FileReader fr = new FileReader(fi);

								// Buffered reader
								BufferedReader br = new BufferedReader(fr);

								// Initialize sl
								sl = br.readLine();

								// Take the input from the file
								while ((s1 = br.readLine()) != null) {
									sl = sl + "\n" + s1;
								}

								// Set the text
								t.setText(sl);
							}
							catch (Exception evt) {
								JOptionPane.showMessageDialog(f, evt.getMessage());
							}
						}
						// If the user cancelled the operation
						else
							JOptionPane.showMessageDialog(f, "Operation Cancelled!!!");
					}
			else if (s.equals("New")) {
						t.setText("");
					}
			else if (s.equals("Close")) {
						System.exit(0);
					}
			else if (s.equals("Select All")) {
						t.selectAll();
					}

			else if (s.equals("About")) {

					    JOptionPane.showMessageDialog(f,"Developed By:\nSourav, Riya, Sayali");
					}

			else if (s.equals("Java"))
	        {
	        	// Set the label to the path of the selected directory
	            File fi = new File("E:/Demo.java");

	            try
	            {
	                FileWriter wr = new FileWriter(fi, false);		// Create a file writer
	                BufferedWriter w = new BufferedWriter(wr);		// Create buffered writer to write
	                w.write(t.getText());		// Write
	                w.flush();
	                w.close();
	                Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"E: && javac Demo.java && java Demo\"");
	            }

	            catch (Exception evt)
	            {
	                JOptionPane.showMessageDialog(f, evt.getMessage());
	            }
	        }

	        else if (s.equals("Python")) {
	        	// Set the label to the path of the selected directory
	            File fi = new File("E:/Demo.py");

	            try
	            {
	                FileWriter wr = new FileWriter(fi, false);		// Create a file writer
	                BufferedWriter w = new BufferedWriter(wr);		// Create buffered writer to write
	                w.write(t.getText());		 // Write
	                w.flush();
	                w.close();
	                Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"E: &&  python Demo.py\"");
	            }

	            catch (Exception evt)
	            {
	                JOptionPane.showMessageDialog(f, evt.getMessage());
	            }
	        }

			else if (e.getSource()== fontfamily)
			  	{

			  	  	JOptionPane.showConfirmDialog(null, familylist, "Choose Font Family", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			  		ffamily=String.valueOf(familylist.getSelectedValue());
			  		font1=new Font(ffamily,fstyle,fsize);
			  		t.setFont(font1);
			  	}
			else if (e.getSource()== fontstyle)
			 	 {

			     	JOptionPane.showConfirmDialog(null, stylelist, "Choose Font Style", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			  		fstyle=stylevalue[stylelist.getSelectedIndex()];
			  		font1=new Font(ffamily,fstyle,fsize);
			  		t.setFont(font1);
			  	 }
			else if (e.getSource()== fontsize)
			  	 {

			     	JOptionPane.showConfirmDialog(null, sizelist, "Choose Font Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			  		fsizestr=String.valueOf(sizelist.getSelectedValue());
			  		fsize =Integer.parseInt(fsizestr);
			  		font1=new Font(ffamily,fstyle,fsize);
  			  		t.setFont(font1);

			  	 }
			else if (s.equals("Choose Color"))
				{
					Color c = JColorChooser.showDialog(this,"Select Color",Color.BLUE);
					t.setForeground(c);
				}
		  }


    public static void main(String [] args)
	{
		NotepadProject n = new NotepadProject();
		n.setVisible(true);
		n.setDefaultCloseOperation(3);
	}
}