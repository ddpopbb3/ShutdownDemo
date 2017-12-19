/*******************************************************************************
 *
 *==============================================================================
 *
 *
 * Created on 2017年8月3日 下午4:14:57
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 可以设定关机时间，取消关机
 * 
 * @author liyp (mailto:liyp@primeton.com)
 */
public class ShutDownDemo extends JFrame {

	private static final long serialVersionUID = 1L;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public static void main(String[] args) {
		new ShutDownDemo();
	}

	boolean isStop = false; // 线程结束标志
	static int totalTime = 0; // 总时间

	// 时分秒输入框
	private JTextField textField = new JTextField();
	private JTextField textField_1 = new JTextField();
	private JTextField textField_2 = new JTextField();
	JButton jbt1 = new JButton("确定");

	// 构造函数
	public ShutDownDemo() {
		setTitle("\u5173\u673A\u7A0B\u5E8F");
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 26, 282, 31);
		getContentPane().add(panel);

		// 时分秒
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

		jbt1.setBounds(29, 0, 95, 59);
		panel_1.add(jbt1);

		JButton jbt2 = new JButton("取消");
		jbt2.setBounds(151, 0, 95, 59);
		panel_1.add(jbt2);

		// 注册取消按钮的监听事件
		jbt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelShutdown();
			}
		});
		// 注册确定按钮的监听事件
		jbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (calcTotalTime(textField.getText(), textField_1.getText(), textField_2.getText())) {
					int isShutdown = JOptionPane.showConfirmDialog(null, "确定要在" + calcLeftTime(totalTime) + "时间关机吗",
						"提示", JOptionPane.YES_NO_OPTION);
					if (isShutdown == 0)
						try {
							executeShutdown();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
				} else
					JOptionPane.showMessageDialog(null, "请输入数字");
			}
		});

		// 主窗口参数设置
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.setSize(308, 201);
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		// 注册关闭窗口的监听事件
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int isclose = JOptionPane.showConfirmDialog(null, "关闭窗口前要取消关机计划吗？", "温馨提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
				if (isclose == 0)
					cancelShutdown();
				else if (isclose == 1)
					System.exit(0);
			}
		});
	}

	// 线程完成计时器效果
	class timeThread extends Thread {
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
	}

	// 计算剩余时间，返回HH:mm:ss
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

	// 参数检查以及计算总时间
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

	// 取消关机程序
	public void cancelShutdown() {
		String[] args = new String[] { "cmd.exe", "/c", "shutdown -a" };
		try {
			Runtime.getRuntime().exec(args); // 核心代码
			JOptionPane.showMessageDialog(null, "取消成功");
			isStop = true;
			dispose(); // 销毁界面，释放一部分资源
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 执行关机程序
	public void executeShutdown() throws InterruptedException {
		calcTotalTime(textField.getText(), textField_1.getText(), textField_2.getText());
		String[] args = new String[] { "cmd.exe", "/c", "shutdown -s -t " + totalTime };
		try {
			final Process process = Runtime.getRuntime().exec(args);
			int value = process.waitFor(); // 执行返回值 0 表示成功 其他表示错误
			// System.out.println(value); // 输出返回值ֵ
			if (value == 0) {
				new timeThread().start();
				JOptionPane.showMessageDialog(null, "定时成功");
				textField.setFocusable(false);
				textField_1.setFocusable(false);
				textField_2.setFocusable(false);
				jbt1.setEnabled(false);
			} else if (value != 0) {
				printMessage(process.getInputStream());
				printMessage(process.getErrorStream());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 打印输出流或者错误流
	private static void printMessage(final InputStream input) {
		// 匿名函数启动线程
		new Thread(new Runnable() {
			public void run() {
				Reader reader = new InputStreamReader(input);
				BufferedReader bf = new BufferedReader(reader);
				String line = null;
				try {
					while ((line = bf.readLine()) != null) {
						// System.out.println(line); //输出错误信息
						JOptionPane.showMessageDialog(null, line); // 弹出框显示输出错误信息
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 检查入参中是否有非数字
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
