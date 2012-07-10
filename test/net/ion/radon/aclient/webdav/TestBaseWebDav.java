package net.ion.radon.aclient.webdav;

import junit.framework.TestCase;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.providers.netty.NettyProvider;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.impl.let.webdav.WebDavLet;
import net.ion.radon.util.AradonTester;

import org.apache.commons.vfs2.provider.ram.RamFileProvider;

public class TestBaseWebDav extends TestCase {
	protected Aradon aradon;

	public TestBaseWebDav() {
	}

	public void setUp() throws Exception {
		FileSystemEntry filesystem = VFS.createEmpty();
		filesystem.addProvider("template", new RamFileProvider());

		aradon = AradonTester.create()
			.mergeSection("webdav").putAttribute(FileSystemEntry.class.getCanonicalName(), filesystem)
			.addLet("/", "webdav", IMatchMode.STARTWITH, WebDavLet.class).getAradon();
		aradon.startServer(9000);
	}

	@Override
	protected void tearDown() throws Exception {
		aradon.stop();
		super.tearDown();
	}

	public NewClient newClient() {
		return NewClient.create();
	}

	public NewClient newHttpClient(ClientConfig config) {
		NewClient client = NewClient.create(new NettyProvider(config), config);
		client.close() ;
		return client;
	}

}
