import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SnipingProgram implements ActionListener {

	static JTextField tf;
	static JPasswordField tf2;
	static JTextField tf4;
	static JLabel l;
	static JLabel pl;
	static JTextField tf5;
	static JTextField tf6;

	static long pinglatency;

	static long[] avgping = new long[10];

	public static void main(String[] args) {

		long sum = 0;

		for (int i = 0; i < 10; i++) {
			avgping[i] = getPing("api.mojang.com");
		}

		for (int i = 0; i < avgping.length; i++) {
			sum += avgping[i];
		}

		pinglatency = sum / avgping.length;

		System.out.println(pinglatency);

		JOptionPane.showMessageDialog(null, "Please get your time delay from time.is.", "Get Delay From time.is",
				JOptionPane.WARNING_MESSAGE);

		setupGUI();

	}
	
	public static void startSnipe(String u, String p, String u2, String at, String d) {
		// sets up date and times
		
		try {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd:MM:yyyy/HH:mm:ss:SSSS");
			String s = d + ":0000";
			LocalDateTime dt = LocalDateTime.parse(s, format);
			LocalDateTime dtt = dt.minus(pinglatency, ChronoField.MILLI_OF_DAY.getBaseUnit());
			LocalDateTime dttt = dtt.minus(Long.parseLong(tf6.getText()), ChronoField.MILLI_OF_DAY.getBaseUnit());
			String s2 = LocalDateTime.now().format(format);
			LocalDateTime dt2 = LocalDateTime.parse(s2, format);

			System.out.println(dttt);
			
			JOptionPane.showMessageDialog(null, "Starting snipe...", "Currently sniping: " + tf.getText(),
					JOptionPane.INFORMATION_MESSAGE);

			if (dttt.isAfter(dt2)) {
				while (dt.isAfter(dt2)) {

					s2 = LocalDateTime.now().format(format);
					dt2 = LocalDateTime.parse(s2, format);
				
				}

				l.setText("Info: Attempting Snipe...");
				JOptionPane.showMessageDialog(null, "Sending POST request to Mojang API...", "Making POST Request...",
						JOptionPane.INFORMATION_MESSAGE);

				for (int i = 0; i < 20; i++) {
					MakeJSONRequest(u, p, u2, at);
				}
			} else {
				l.setText("Info: That time has already passed");
				
				JOptionPane.showMessageDialog(null, "That time has passed", "Error: Time has passed.",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (DateTimeException | NumberFormatException e1) {
			l.setText("Info: Invaild Date or time delay");
			JOptionPane.showMessageDialog(null, "That time/delay is Invaild", "Error: Time is Invaild.",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void MakeJSONRequest(String username, String password, String uuid, String authToken) {
		l.setText("Info: Making request...");
		
		try {
			@SuppressWarnings("unused")
			HttpResponse<String> response = Unirest.post("https://api.mojang.com/user/profile/" + uuid + "/name")
					.header("content-type", "application/json").header("authorization", "" + authToken + "")
					.body("{\"name\":\"" + username + "\",\"password\":\"" + password + "\"}").asString();
			l.setText("Info: Request successful.");
		} catch (UnirestException e) {
			l.setText("Info: Request faied.");
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		long sum = 0;

		for (int i = 0; i < 10; i++) {
			avgping[i] = getPing("api.mojang.com");
		}

		for (int i = 0; i < avgping.length; i++) {
			sum += avgping[i];
		}

		pinglatency = sum / avgping.length;
		
		pl.setText("Ping: " + pinglatency);
		
		String username = tf.getText();
		@SuppressWarnings("deprecation")
		String password = tf2.getText();
		String date = tf5.getText();
		String email = tf4.getText();

		String authToken = "Bearer " + getAuthCode(email, password);
		String uuid = getUUID(email, password);

		System.out.println(authToken);
		System.out.println(uuid);

		if (uuid != "" && authToken != "Bearer ") {
			startSnipe(username, password, uuid, authToken, date);
		}
	}
	
	public static void setupGUI() {
		Image icon = null;
		try {
			URL url = new URL("https://cdn2.iconfinder.com/data/icons/font-awesome/1792/code-512.png");
			icon = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFrame f = new JFrame();
		f.setSize(840, 420);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("ESP: Een's Sniping Program");
		f.setIconImage(icon);

		JPanel p = new JPanel();
		f.add(p);

		p.setLayout(null);
		
		l = new JLabel("Info: Idle...");
		l.setBounds(10, 20, 420, 25);
		p.add(l);
		
		pl = new JLabel("Ping: " + pinglatency);
		pl.setBounds(10, 40, 420, 25);
		p.add(pl);

		JLabel l2 = new JLabel("Wanted Username:");
		l2.setBounds(10, 80, 160, 25);
		p.add(l2);

		tf = new JTextField(10);
		tf.setBounds(125, 80, 165, 25);
		p.add(tf);

		JLabel l3 = new JLabel("Account Password:");
		l3.setBounds(10, 140, 160, 25);
		p.add(l3);

		tf2 = new JPasswordField(10);
		tf2.setBounds(125, 140, 165, 25);
		p.add(tf2);

		JLabel l5 = new JLabel("Account Email:");
		l5.setBounds(10, 110, 160, 25);
		p.add(l5);

		tf4 = new JTextField(10);
		tf4.setBounds(125, 110, 165, 25);
		p.add(tf4);

		JLabel l6 = new JLabel("Date (24 hr) (dd:MM:yyyy/hh:mm:ss):");
		l6.setBounds(10, 200, 225, 25);
		p.add(l6);

		tf5 = new JTextField(10);
		tf5.setBounds(225, 200, 165, 25);
		p.add(tf5);

		JLabel l7 = new JLabel("Time delay (in milliseconds, negative if behind): ");
		l7.setBounds(10, 230, 300, 25);
		p.add(l7);

		tf6 = new JTextField(10);
		tf6.setBounds(280, 230, 165, 25);
		p.add(tf6);

		JButton b = new JButton("Start");
		b.setBounds(10, 270, 280, 25);
		p.add(b);

		b.addActionListener(new SnipingProgram());

		f.setVisible(true);
	}

	static long getPing(String hostAddress) {
		InetAddress inetAddress = null;
		Date start, stop;

		try {
			inetAddress = InetAddress.getByName(hostAddress);
		} catch (UnknownHostException e) {
			System.out.println("Problem, unknown host:");
			e.printStackTrace();
		}

		try {
			start = new Date();
			if (inetAddress.isReachable(5000)) {
				stop = new Date();
				return (stop.getTime() - start.getTime());
			}

		} catch (IOException e1) {
			System.out.println("Problem, a network error has occurred:");
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			System.out.println("Problem, timeout was invalid:");
			e1.printStackTrace();
		}

		return -1; // to indicate failure

	}

	public static String getUUID(String e, String p) {
		String uuid = "";

		try {
			HttpResponse<JsonNode> response = Unirest.post("https://authserver.mojang.com/authenticate")
					.header("content-type", "application/json")
					.body("{\n    \"username\": \"" + e + "\",     \n                        \n    \"password\": \"" + p
							+ "\",\n     \n    \"requestUser\": true               \n}")
					.asJson();

			uuid = response.getBody().getObject().getJSONObject("user").getString("id");
		} catch (UnirestException | JSONException e1) {
			l.setText("Info: Invaild email or password");
			JOptionPane.showMessageDialog(null, "Password or Email is invaild.", "Error: Invaild Email + Password.",
					JOptionPane.ERROR_MESSAGE);
		}

		return uuid;
	}

	public static String getAuthCode(String email, String password) {
		String authToken = "";

		try {

			HttpResponse<JsonNode> response = Unirest.post("https://authserver.mojang.com/authenticate")
					.header("content-type", "application/json")
					.body("{\n    \"username\": \"" + email + "\",     \n                        \n    \"password\": \""
							+ password + "\",\n     \n    \"requestUser\": true               \n}")
					.asJson();
			authToken = response.getBody().getObject().getString("accessToken");
		} catch (JSONException | UnirestException e) {
			l.setText("Info: Invaild email or password");
		}

		return authToken;
	}
}
