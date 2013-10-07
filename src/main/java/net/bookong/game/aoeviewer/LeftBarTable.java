package net.bookong.game.aoeviewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

/**
 * @author jiangxu
 * 
 */
public class LeftBarTable extends JTable {
	private static final long serialVersionUID = 1L;
	private TableModel tableModel = new TableModel();

	public LeftBarTable() {
		setModel(tableModel);
		setBorder(BorderFactory.createLineBorder(Color.black));
		setOpaque(true);
		setSelectionMode(0);
		setFocusable(false);
		getTableHeader().setReorderingAllowed(false);
	}

	class TableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private List<Object[]> rowDatas = new ArrayList<Object[]>();

		public String getColumnName(int column) {
			return "文件名称";
		}

		public int getRowCount() {
			return rowDatas.size();
		}

		public int getColumnCount() {
			return 1;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return rowDatas.get(rowIndex)[0];
		}

		public List<Object[]> getRowDatas() {
			return rowDatas;
		}
	}

	public List<Object[]> getRowDatas() {
		return tableModel.getRowDatas();
	}

	public void valueChanged(ListSelectionEvent lse) {
		super.valueChanged(lse);
		if (getSelectedRow() != -1 && !lse.getValueIsAdjusting()) {
			Object rowData[] = (Object[]) tableModel.getRowDatas().get(getSelectedRow());
			MainFrame.getInstance().selectFile((Long) rowData[1]);
		}
	}
}