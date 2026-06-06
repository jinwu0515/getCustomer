#target photoshop

app.displayDialogs = DialogModes.NO;

var log = new File("D:/codexFile/outputs/ai-business-demo/photoshop_save_log.txt");
log.open("w");

try {
  if (!app.documents.length) {
    throw new Error("No open Photoshop document.");
  }
  var doc = app.activeDocument;
  var out = new File("D:/codexFile/outputs/ai-business-demo/doraemon_photoshop.png");
  var png = new PNGSaveOptions();
  doc.saveAs(out, png, true, Extension.LOWERCASE);
  log.writeln("saved:" + out.fsName);
} catch (e) {
  log.writeln("error:" + e);
} finally {
  log.close();
}
