package helpers;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
	Klasa filter fajl koji prihvata sve fajlove sa datim skupom ekstenzija.
*/
public class ExtensionHelper extends FileFilter 
{

	/**
	    Konstruiše ekstenziju fajla.
	    @param description je vrsta (npr. "Woozle files")
	    @param extensions prihvaæene ekstenzije (npr. new String[] { ".woozle", ".wzl" })
	*/
	public ExtensionHelper(String description, String[] extensions) {
		this.description = description;
		this.extensions = extensions;
	}

	/**
	    Konstruiše ekstenziju fajla.
		@param description je vrsta (npr. "Woozle files")
		@param extensions prihvaæene ekstenzije, odvojene sa | (npr.".woozle|.wzl" })
	*/
	public ExtensionHelper(String description, String extensions) {
		this.description = description;
		StringTokenizer tokenizer = new StringTokenizer(extensions, "|");
		this.extensions = new String[tokenizer.countTokens()];
		for (int i = 0; i < this.extensions.length; i++)
			this.extensions[i] = tokenizer.nextToken();
	}

	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		String fname = f.getName().toLowerCase();
		for (int i = 0; i < extensions.length; i++)
			if (fname.endsWith(extensions[i].toLowerCase()))
				return true;
		return false;
	}

	public String getDescription() {
		return description;
	}

	public String[] getExtensions() {
		return extensions;
	}

	private String description;
	private String[] extensions;
}
