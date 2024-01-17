using System.Net;
using System.Text;
using System.Drawing;
using System.Diagnostics;

class Program
{

	static string recdirectory;

	static void Main(string[] args)
	{
		HttpListener server = new HttpListener();

		IPAddress[] addresses = Dns.GetHostAddresses(Dns.GetHostName());
		for (int a = 0; a < addresses.Length; ++a)
		{
			if (addresses[a].ToString().Length < 15)
			{
				server.Prefixes.Add("http://" + addresses[a].ToString() + ":8888/test/");
			}
		}

		server.Start();

		recdirectory = "./Recognizer/dist/raspoznavalka/";

		while (true)
		{
			try
			{
				var context = server.GetContext(); // Получает контекст подключения, из которого может взять информацию о запросе (Request) и создать ответ (Responce).

				HttpListenerRequest request = context.Request;
				HttpListenerResponse response = context.Response;

				if (request.Headers["DeviceType"] == "Observer")
				{

					WriteLog("Принят запрос от наблюдателя (" + request.ContentLength64.ToString() + " байт). IP: " + request.RemoteEndPoint);

					Image imgo = ReadImage(request);
					string fname = "./Original/IFO" + DateTime.Now.ToString().Replace('.', '-').Replace(':', '-') + ".jpg";

					imgo.Save(fname, System.Drawing.Imaging.ImageFormat.Jpeg);
					WriteLog("Сохранено изображение " + fname);

					imgo.Save(recdirectory + "file.jpeg", System.Drawing.Imaging.ImageFormat.Jpeg);
					imgo.Dispose();

					if (OpenRecognizer())
					{
						WriteLog("Изображение от наблюдателя распознано. ");

						Image imgr = Image.FromFile(recdirectory + "cam.jpeg");
						fname = "./Recognized/RI" + DateTime.Now.ToString().Replace('.', '-').Replace(':', '-') + ".jpg";

						imgr.Save(fname, System.Drawing.Imaging.ImageFormat.Jpeg);
						WriteLog("Сохранено изображение " + fname);
						imgr.Dispose();

						response.ContentLength64 = 5;
						response.OutputStream.Write(new byte[5] { 1, 0, 1, 0, 1 });
						response.OutputStream.Flush();

						WriteLog("Запрос наблюдателя обработан. IP: " + request.RemoteEndPoint);
					}
					else
					{
						WriteLog("Истекло время ожидания работы распознавателя. ");
					}


				}
				else if (request.Headers["DeviceType"] == "Client")
				{

					WriteLog("Принят запрос от клиента. IP: " + request.RemoteEndPoint);

					string[] rfiles = Directory.GetFiles("./Recognized");
					string fname = rfiles[rfiles.Length - 1];

					Image imgr = Image.FromFile(fname);
					byte[] answerimg = CreateResopnse(ref imgr);
					response.ContentLength64 = answerimg.Length;

					Stream output = response.OutputStream;
					output.Write(answerimg);
					//output.Flush();

					WriteLog("Ответ клиенту отправлен (" + answerimg.Length.ToString() + " байт). IP: " + request.RemoteEndPoint);

				}
				else
				{
					WriteLog("Принят запрос от неизвестного устройства. IP: " + request.RemoteEndPoint);
					response.OutputStream.Write(new byte[] { 0, 1 });
					response.OutputStream.Flush();
				}
			}
			catch (Exception exception)
			{
				WriteLog("Вызвано исключение: " + exception.Message);
			}
		}
	}


	static Image ReadImage(HttpListenerRequest request)
	{
		byte[] barray = new byte[request.ContentLength64];
		Stream input = request.InputStream;
		int bytescount = input.Read(barray);
		MemoryStream ms = new MemoryStream(barray);
		Image imgo = Image.FromStream(ms);
		return imgo;
	}


	static byte[] CreateResopnse(ref Image imgr)
	{
		MemoryStream ms = new MemoryStream();
		imgr.Save(ms, imgr.RawFormat);
		byte[] imgba = ms.GetBuffer();
		imgr.Dispose();
		return imgba;
	}


	static bool OpenRecognizer()
	{
		ProcessStartInfo psi = new ProcessStartInfo();
		psi.WorkingDirectory = recdirectory;
		psi.FileName = recdirectory + "raspoznavalka.exe";


		Process recognizer = Process.Start(psi);
		//timer
		recognizer.WaitForExit();
		recognizer.Close();
		return true;
	}


	static void WriteLog(string text)
	{
		StreamWriter sw = new StreamWriter("log.txt", true);
		string logtext = DateTime.Now.ToString() + " " + text + "\n";
		sw.Write(logtext);
		sw.Close();
	}

}