package it.solvingteam.ftptest

import static org.junit.Assert.*
import org.junit.*

import org.apache.commons.net.ftp.FTPClient
import org.mockftpserver.fake.FakeFtpServer

import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import java.io.IOException;
import java.util.List;

class FTPClientTests {

    private static final String HOME_DIR = "/";
    private static final String FILE = "/dir/sample.txt";
    private static final String CONTENTS = "abcdef 1234567890";

    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";

    def fakeFtpServer

    def server = 'localhost'
    def port = 0

    @Before
    void setUp() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new FileEntry(FILE, CONTENTS));
        fakeFtpServer.setFileSystem(fileSystem);

        UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, HOME_DIR);
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();
        port = fakeFtpServer.getServerControlPort();
   }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testDependencies() {
        new FTPClient()
        new FakeFtpServer()
    }

    @Test
    void testReadFileFailing() {

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(USERNAME, PASSWORD);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ftpClient.retrieveFile('aaa.txt', outputStream);
        ftpClient.disconnect();


        assert !success
    }

    @Test
    void testReadFileSuccess() {

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(USERNAME, PASSWORD);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ftpClient.retrieveFile(FILE, outputStream);
        ftpClient.disconnect();


        assert success
    }

    @Test
    void testWriteFile() {

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(USERNAME, PASSWORD);
       
        File f = new File('hello.txt')
        f << 'ciao!!'

        def inputStream = new FileInputStream(f)
        boolean success = ftpClient.storeFile("/ciao.txt", inputStream)
       
        assert success

    }
}
