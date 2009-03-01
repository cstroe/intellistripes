package org.intellij.stripes.facet.tabs;

import com.intellij.facet.ui.libraries.LibraryInfo;
import com.intellij.facet.ui.libraries.MavenLibraryUtil;

public enum FileUploadImpl {
	NONE("None") {
		public LibraryInfo getLibraryInfo() {
			return null;
		}
	}, COMMONS("Commons FileUpload") {
		public LibraryInfo getLibraryInfo() {
			return COMMONS_FILE_UPLOAD_LIBRARY_INFO;
		}
	}, COS("COS") {
		public LibraryInfo getLibraryInfo() {
			return COS_LIBRARY_INFO;
		}
	};

	private static final LibraryInfo COMMONS_FILE_UPLOAD_LIBRARY_INFO = MavenLibraryUtil.createMavenJarInfo("commons-fileupload", "1.2.1", "org.apache.commons.fileupload.FileItem");
	private static final LibraryInfo COS_LIBRARY_INFO = MavenLibraryUtil.createMavenJarInfo("cos", "05Nov2002", "com.oreilly.servlet.multipart.FilePart");

	private String myName;

	FileUploadImpl(String s) {
		this.myName = s;
	}

	@Override
	public String toString() {
		return this.myName;
	}

	public abstract LibraryInfo getLibraryInfo();
}
