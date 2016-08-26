package net.bookong.game.aoeviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.bookong.tools.archive.drs.DrsPackage;
import net.bookong.tools.archive.drs.FileInfo;
import net.bookong.tools.archive.slp.Decoder;
import net.bookong.tools.archive.slp.Frame;
import net.bookong.tools.utils.ByteUtils;

/**
 * @author jiangxu
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_WIDTH = 1116;
	private static final  int WINDOW_HEIGHT = 686;
	
	private static MainFrame instance = new MainFrame();
	public static MainFrame getInstance() {
		return instance;
	}
	
	// UI
	private LeftBarTable leftBarTable;
	private JLabel stateBar;
	private JPanel contentPane = new JPanel();
	private JScrollPane hexDumpPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JTextArea txtHexDump = new JTextArea();
	private Component currContentComp;
	
	private Settings appSettings = new Settings();
	private String[] playerNames = new String[] { "蓝色", "红色", "绿色", "黄色", "橙色", "青色", "紫色", "灰色", };
	
	// DRS and SLP 
	private DrsPackage drsPackage = new DrsPackage();
	private Map<Long, String> fileNames = new HashMap<Long, String>();
	private Integer[] palette = new Integer[256];
	public Long currFileNo;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance.setVisible(true);
					new Thread(Daemon.getInstance()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainFrame() {
		setAlwaysOnTop(true);
		setResizable(false);
		
		setFont(new Font("宋体", Font.PLAIN, 14));
		setTitle("帝国时代资源浏览器");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		Font font = new Font("宋体", Font.PLAIN, 12);
		UIManager.put("JMenuBar.font", font);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("文件");
		menuBar.add(mnNewMenu);
		
		JMenuItem miOpenDrsFile = new JMenuItem("打开DRS文件");
		mnNewMenu.add(miOpenDrsFile);
		miOpenDrsFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("帝国时代资源文件包","drs"));
				chooser.setDialogTitle("读取帝国时代资源文件包");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setSelectedFile(new File(appSettings.getProperty(Settings.LAST_SELECT_FILE, "")));

				if (chooser.showOpenDialog(instance) == JFileChooser.APPROVE_OPTION) {
					String filePath = chooser.getSelectedFile().getPath();
					if(!filePath.toLowerCase().endsWith(".drs")){
						JOptionPane.showMessageDialog(instance, "文件类型错", "错误",JOptionPane.ERROR_MESSAGE);
						return;
					}
					appSettings.setProperty(Settings.LAST_SELECT_FILE, filePath);
				
					drsPackage.open(new File(filePath));
					
					leftBarTable.getRowDatas().clear();
					for(Entry<Long, FileInfo> entry : drsPackage.getDataFiles().entrySet()){
						FileInfo fi = entry.getValue();
						String filename = fileNames.get(fi.fileNo);
						
						Object[] rowData = new Object[2];
						rowData[0] = (filename == null ? (fi.fileNo) : filename);
						rowData[1] = fi.fileNo;
						leftBarTable.getRowDatas().add(rowData);
					}
					leftBarTable.updateUI();
				}
			}
		});
		
		JMenu mnNewMenu_1 = new JMenu("显示");
		menuBar.add(mnNewMenu_1);
		
		JMenu mnNewMenu_2 = new JMenu("玩家");
		mnNewMenu_1.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("蓝色");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 0;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("红色");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 1;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("绿色");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 2;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("黄色");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 3;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_4);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("橙色");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 4;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_5);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("青色");
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 5;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_6);
		
		JMenuItem mntmNewMenuItem_7 = new JMenuItem("紫色");
		mntmNewMenuItem_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 6;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_7);
		
		JMenuItem mntmNewMenuItem_8 = new JMenuItem("灰色");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().currPlayerId = 7;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_8);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("显示/隐藏轮廓");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AoeCanvas.getInstance().ifShowOutlines = !AoeCanvas.getInstance().ifShowOutlines;
				AoeCanvas.getInstance().reloadImages();
				flushStateBar();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel leftBar = new JPanel();
		leftBar.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.add(leftBar, BorderLayout.WEST);
		leftBar.setLayout(new BorderLayout(0, 0));
		JScrollPane leftBarScrollPane = new JScrollPane();
		leftBar.add(leftBarScrollPane);
		leftBar.setPreferredSize(new Dimension(200,1));
		
		stateBar = new JLabel("ready");
		stateBar.setBorder(new TitledBorder(""));
		contentPane.add(stateBar, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				beforeQuit();
			}
		});
		
		leftBarTable = new LeftBarTable();
		leftBarScrollPane.getViewport().setView(leftBarTable);
		
		hexDumpPane.getViewport().setView(txtHexDump);
		txtHexDump.setFont(new Font("宋体", Font.PLAIN, 14));
		txtHexDump.setEditable(false);
		
		loadPalette(MainFrame.class.getResource("/Aoe2Palette.pal").getPath());
		loadFileNames(MainFrame.class.getResource("/Aoe2ListFile.txt").getPath());
	}
	
	private void flushStateBar(){
		StringBuilder sb = new StringBuilder();
		sb.append("打开文件：").append(appSettings.getProperty(Settings.LAST_SELECT_FILE, ""));
		sb.append("    [玩家颜色：").append(playerNames[AoeCanvas.getInstance().currPlayerId]).append("]");
		if(AoeCanvas.getInstance().ifShowOutlines){
			sb.append("[显示轮廓]");
		}else{
			sb.append("[隐藏轮廓]");
		}
		stateBar.setText(sb.toString());
		stateBar.setToolTipText(sb.toString());
		stateBar.updateUI();
	}
	
	private void beforeQuit(){
		drsPackage.close();
		Daemon.getInstance().isRunning = false;
	}
	
	private void loadPalette(String filepath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			for(int i=0; i<256; i++){
				String[] strs = br.readLine().split(" ");
				int r = Integer.valueOf(strs[0].trim());
				int g = Integer.valueOf(strs[1].trim());
				int b = Integer.valueOf(strs[2].trim());
				
				palette[i] = 0xFF000000 | r << 16 | g << 8 | b;
			}
		} catch (IOException e) {
			throw new RuntimeException("Fail to load palette.", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadFileNames(String filepath) {
		fileNames.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			for(int i=0; i<256; i++){
				String[] strs = br.readLine().split(" ");
				fileNames.put(Long.valueOf(strs[0].trim()), strs[1]);
			}
		} catch (IOException e) {
			throw new RuntimeException("Fail to load file names.", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void selectFile(Long fileNo){
		currFileNo = fileNo;
		byte[] buff = drsPackage.readFileData(fileNo);
		boolean isSlpFile = false;
		if (buff.length > 32) {
			byte[] version = new byte[Decoder.FILE_HEADER_VERSION.length];
			System.arraycopy(buff, 0, version, 0, Decoder.FILE_HEADER_VERSION.length);
			byte[] comment = new byte[Decoder.FILE_HEADER_COMMENT.length];
			System.arraycopy(buff, 8, comment, 0, Decoder.FILE_HEADER_COMMENT.length);
			
			if (Arrays.equals(Decoder.FILE_HEADER_VERSION, version)
					&& Arrays.equals(Decoder.FILE_HEADER_COMMENT, comment)) {
				isSlpFile = true;
			}
		}
		
		if (currContentComp != null) {
			contentPane.remove(currContentComp);
		}

		if (isSlpFile) {
			Frame[] frames = Decoder.decode(buff);
			AoeCanvas.getInstance().reloadImages(frames, palette);
			currContentComp = AoeCanvas.getInstance();
		} else {
			txtHexDump.setText(ByteUtils.hexDump(buff, 0, buff.length));
			currContentComp = hexDumpPane;
		}
		
		contentPane.add(currContentComp, BorderLayout.CENTER);
		contentPane.updateUI();
	}
}
