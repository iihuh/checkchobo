const express = require("express");
const multer = require("multer");
const path = require("path");
const cors = require("cors");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.static("public"));
app.use("/videos", express.static("uploads"));
// Configure storage
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "uploads/");
  },
  filename: function (req, file, cb) {
    const uniqueName = Date.now() + "-" + file.originalname;
    cb(null, uniqueName);
  },
});

// Only accept video files
const upload = multer({
  storage: storage,
  limits: { fileSize: 100 * 1024 * 1024 }, // 100MB
  fileFilter: (req, file, cb) => {
    if (file.mimetype.startsWith("video/")) {
      cb(null, true);
    } else {
      cb(new Error("Only video files allowed!"), false);
    }
  },
});

// Upload endpoint
app.post("/upload", upload.single("video"), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ message: "No video uploaded" });
  }

  // ⏱️ Timestamped messages (seconds)
  const timelineText = [
    { time: 0, text: "Video started" },
    { time: 3, text: "Animal detected in frame" },
    { time: 7, text: "Animal is moving" },
    { time: 12, text: "Animal stopped" },
    { time: 18, text: "Video analysis complete" }
  ];

  res.json({
    filename: req.file.filename,
    timeline: timelineText
  });
});


app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
