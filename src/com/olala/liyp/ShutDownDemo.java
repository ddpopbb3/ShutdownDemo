/*******************************************************************************
 *
 *==============================================================================
 *
 *
 * Created on 2017��8��3�� ����4:14:57
 *******************************************************************************/
package com.olala.liyp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * һ����UI�������ػ�С��������
 * 
 * @author liyp (mailto:liyp@primeton.com)
 */
public class ShutDownDemo extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ShutDownDemo();
	}

	boolean isStop = false; // �߳̽�����־
	static int totalTime = 0; // ��ʱ��
	private JTextField textField = new JTextField();
	private JTextField textField_1 = new JTextField();
	private JTextField textField_2 = new JTextField();

	// ���캯��
	public ShutDownDemo() {
		setTitle("\u5173\u673A\u7A0B\u5E8F");
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 26, 282, 31);
		getContentPane().add(panel);

		// ʱ �� ��
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textField);
		textField.setColumns(2);

		JLabel lblNewLabel = new JLabel("\u65F6");
		panel.add(lblNewLabel);

		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textField_1);
		textField_1.setColumns(2);

		JLabel lblNewLabel_1 = new JLabel("\u5206");
		panel.add(lblNewLabel_1);

		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textField_2);
		textField_2.setColumns(2);

		JLabel lblNewLabel_2 = new JLabel("\u79D2");
		panel.add(lblNewLabel_2);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 93, 282, 59);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		final JButton jbt1 = new JButton("ȷ��");
		jbt1.setBounds(29, 0, 95, 59);
		panel_1.add(jbt1);

		JButton jbt2 = new JButton("ȡ��");
		jbt2.setBounds(151, 0, 95, 59);
		panel_1.add(jbt2);

		// ע��ȡ����ť�ļ����¼�
		jbt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelShutdown();
			}
		});
		// ע��ȷ����ť�ļ����¼�
		jbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (calcTotalTime(textField.getText(), textField_1.getText(), textField_2.getText())) {
					int isShutdown = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ��" + calcLeftTime(totalTime) + "ʱ���ػ���",
							"��ʾ", JOptionPane.YES_NO_OPTION);
					if (isShutdown == 0)
						try {
							executeShutdown();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
				} else
					JOptionPane.showMessageDialog(null, "���������֣�");
			}
		});

		// �����ڲ�������
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize(308, 201);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int isclose = JOptionPane.showConfirmDialog(null, "�رմ���ǰҪȡ���ػ��ƻ���", "��ܰ��ʾ", JOptionPane.YES_NO_OPTION);
				if (isclose == 0)
					cancelShutdown();
				// System.exit(0);
			}
		});
	}

	@Override
	public void run() {
		while (!isStop) {
			String time = calcLeftTime(totalTime);
			String[] list = time.split(":");
			textField.setText(list[0]);
			textField_1.setText(list[1]);
			textField_2.setText(list[2]);
			try {
				Thread.sleep(1000);
				totalTime -= 1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// ����ʣ��ʱ�䣬����HH:mm:ss
	public String calcLeftTime(int secondTime) {
		String hour = null;
		String minute = null;
		String second = null;
		String time = null;
		int remain = 0;
		hour = String.valueOf(secondTime / 3600);
		remain = secondTime % 3600;
		minute = String.valueOf(remain / 60);
		second = String.valueOf(remain % 60);
		time = hour + ":" + minute + ":" + second;
		return time;
	}

	// ��������Լ�������ʱ��
	public boolean calcTotalTime(String hour, String minute, String second) {
		if (StringUtils.isBlank(second)) {
			second = "0";
		}
		if (StringUtils.isBlank(minute)) {
			minute = "0";
		}
		if (StringUtils.isBlank(hour)) {
			hour = "0";
		}
		if (isNumeric(hour) && isNumeric(minute) && isNumeric(second)) {
			totalTime = Integer.parseInt(hour) * 3600 + Integer.parseInt(minute) * 60 + Integer.parseInt(second);
			return true;
		} else
			return false;

	}

	// ȡ���ػ�����
	public void cancelShutdown() {
		String[] args = new String[] { "cmd.exe", "/c", "shutdown -a" };
		try {
			Runtime.getRuntime().exec(args); // ���Ĵ���
			JOptionPane.showMessageDialog(null, "ȡ���ɹ�");
			isStop = true;
			dispose(); // ���ٽ��棬�ͷ�һ������Դ
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// ִ�йػ�����
	public void executeShutdown() throws InterruptedException {
		calcTotalTime(textField.getText(), textField_1.getText(), textField_2.getText());
		String[] args = new String[] { "cmd.exe", "/c", "shutdown -s -t " + totalTime };
		try {
			final Process process = Runtime.getRuntime().exec(args);
			int value = process.waitFor(); // ִ�з���ֵ 0 ��ʾ�ɹ� ������ʾ����
			// System.out.println(value); // �������ֵ
			if (value == 0) {
				ShutDownDemo helper = new ShutDownDemo();
				new Thread(helper).start();
				helper.setVisible(true);
				JOptionPane.showMessageDialog(null, "��ʱ�ɹ���");
				dispose();
			} else if (value != 0) {
				printMessage(process.getInputStream());
				printMessage(process.getErrorStream());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// ��ӡ��������ߴ�����
	private static void printMessage(final InputStream input) {
		// �������������߳�
		new Thread(new Runnable() {
			public void run() {
				Reader reader = new InputStreamReader(input);
				BufferedReader bf = new BufferedReader(reader);
				String line = null;
				try {
					while ((line = bf.readLine()) != null) {
						// System.out.println(line); //���������Ϣ
						JOptionPane.showMessageDialog(null, line); // ��������ʾ���������Ϣ
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// ���������Ƿ��з�����
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i)); //���ڵ���
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
