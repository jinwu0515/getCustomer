#target photoshop

app.displayDialogs = DialogModes.NO;

var W = 900;
var H = 900;
var doc = app.documents.add(
  UnitValue(W, "px"),
  UnitValue(H, "px"),
  72,
  "Doraemon_by_Codex",
  NewDocumentMode.RGB,
  DocumentFill.WHITE
);

function rgb(r, g, b) {
  var c = new SolidColor();
  c.rgb.red = r;
  c.rgb.green = g;
  c.rgb.blue = b;
  return c;
}

var BLACK = rgb(18, 22, 26);
var BLUE = rgb(0, 156, 220);
var WHITE = rgb(255, 255, 255);
var RED = rgb(229, 32, 45);
var YELLOW = rgb(255, 213, 58);

function layer(name) {
  var l = doc.artLayers.add();
  l.name = name;
  doc.activeLayer = l;
  return l;
}

function ellipsePoints(cx, cy, rx, ry, steps) {
  var pts = [];
  for (var i = 0; i < steps; i++) {
    var a = (Math.PI * 2 * i) / steps;
    pts.push([cx + Math.cos(a) * rx, cy + Math.sin(a) * ry]);
  }
  return pts;
}

function fillShape(name, points, color) {
  layer(name);
  doc.selection.select(points, SelectionType.REPLACE, 0, true);
  doc.selection.fill(color, ColorBlendMode.NORMAL, 100, false);
  doc.selection.deselect();
}

function ellipse(name, cx, cy, rx, ry, color) {
  fillShape(name, ellipsePoints(cx, cy, rx, ry, 96), color);
}

function strokedEllipse(name, cx, cy, rx, ry, fill, stroke, width) {
  ellipse(name + " outline", cx, cy, rx + width, ry + width, stroke);
  ellipse(name, cx, cy, rx, ry, fill);
}

function linePolygon(x1, y1, x2, y2, width) {
  var dx = x2 - x1;
  var dy = y2 - y1;
  var len = Math.sqrt(dx * dx + dy * dy);
  if (len < 0.01) return [[x1, y1], [x1, y1], [x1, y1], [x1, y1]];
  var nx = (-dy / len) * width / 2;
  var ny = (dx / len) * width / 2;
  return [[x1 + nx, y1 + ny], [x2 + nx, y2 + ny], [x2 - nx, y2 - ny], [x1 - nx, y1 - ny]];
}

function line(name, x1, y1, x2, y2, width, color) {
  fillShape(name, linePolygon(x1, y1, x2, y2, width), color);
  ellipse(name + " cap a", x1, y1, width / 2, width / 2, color);
  ellipse(name + " cap b", x2, y2, width / 2, width / 2, color);
}

function strokedLine(name, x1, y1, x2, y2, width, color, stroke, strokeWidth) {
  line(name + " outline", x1, y1, x2, y2, width + strokeWidth * 2, stroke);
  line(name, x1, y1, x2, y2, width, color);
}

function arc(name, cx, cy, rx, ry, startDeg, endDeg, width, color) {
  var prev = null;
  var steps = 36;
  for (var i = 0; i <= steps; i++) {
    var t = startDeg + ((endDeg - startDeg) * i) / steps;
    var a = (Math.PI * t) / 180;
    var p = [cx + Math.cos(a) * rx, cy + Math.sin(a) * ry];
    if (prev) line(name + " " + i, prev[0], prev[1], p[0], p[1], width, color);
    prev = p;
  }
}

function rect(name, x, y, w, h, color) {
  fillShape(name, [[x, y], [x + w, y], [x + w, y + h], [x, y + h]], color);
}

// Back limbs and body.
strokedLine("left blue arm", 310, 530, 230, 650, 42, BLUE, BLACK, 7);
strokedLine("right blue arm", 590, 530, 670, 650, 42, BLUE, BLACK, 7);
strokedEllipse("left hand", 220, 665, 46, 46, WHITE, BLACK, 7);
strokedEllipse("right hand", 680, 665, 46, 46, WHITE, BLACK, 7);
strokedEllipse("body", 450, 635, 205, 235, BLUE, BLACK, 8);
strokedEllipse("left foot", 335, 842, 108, 42, WHITE, BLACK, 7);
strokedEllipse("right foot", 565, 842, 108, 42, WHITE, BLACK, 7);
line("foot divide", 450, 794, 450, 872, 6, BLACK);
strokedEllipse("belly", 450, 650, 150, 160, WHITE, BLACK, 6);
arc("pocket smile", 450, 663, 103, 72, 18, 162, 6, BLACK);
line("pocket top", 350, 648, 550, 648, 6, BLACK);

// Collar and bell.
rect("collar outline", 275, 493, 450, 34, BLACK);
rect("collar", 282, 500, 436, 21, RED);
strokedEllipse("bell", 450, 545, 48, 48, YELLOW, BLACK, 7);
line("bell groove", 414, 534, 486, 534, 6, BLACK);
ellipse("bell center", 450, 554, 9, 9, BLACK);
line("bell slit", 450, 560, 450, 592, 5, BLACK);

// Head and face.
strokedEllipse("head", 450, 275, 235, 235, BLUE, BLACK, 9);
strokedEllipse("face", 450, 324, 178, 168, WHITE, BLACK, 6);

// Eyes.
strokedEllipse("left eye", 402, 205, 52, 72, WHITE, BLACK, 6);
strokedEllipse("right eye", 500, 205, 52, 72, WHITE, BLACK, 6);
ellipse("left pupil", 421, 228, 12, 18, BLACK);
ellipse("right pupil", 481, 228, 12, 18, BLACK);
ellipse("left eye shine", 425, 219, 4, 5, WHITE);
ellipse("right eye shine", 485, 219, 4, 5, WHITE);

// Nose, mouth and whiskers.
strokedEllipse("nose", 450, 286, 35, 35, RED, BLACK, 5);
ellipse("nose shine", 462, 273, 8, 8, WHITE);
line("nose bridge", 450, 323, 450, 373, 7, BLACK);
arc("mouth", 450, 343, 118, 88, 24, 156, 8, BLACK);
line("left whisker top", 415, 302, 292, 262, 6, BLACK);
line("left whisker middle", 410, 329, 282, 329, 6, BLACK);
line("left whisker bottom", 415, 354, 294, 392, 6, BLACK);
line("right whisker top", 485, 302, 608, 262, 6, BLACK);
line("right whisker middle", 490, 329, 618, 329, 6, BLACK);
line("right whisker bottom", 485, 354, 606, 392, 6, BLACK);

// Tiny finishing touches.
ellipse("cheek blush left", 354, 378, 12, 6, rgb(255, 209, 212));
ellipse("cheek blush right", 546, 378, 12, 6, rgb(255, 209, 212));

doc.activeLayer = doc.layers[0];
var out = new File("D:/codexFile/outputs/ai-business-demo/doraemon_photoshop.png");
var png = new PNGSaveOptions();
doc.saveAs(out, png, true, Extension.LOWERCASE);
