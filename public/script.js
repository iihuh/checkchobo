const input = document.getElementById("videoInput");
const video = document.getElementById("videoPlayer");
const appContainer = document.getElementById("appContainer");
const chatBox = document.getElementById("chatBox");
const activeSubtitle = document.getElementById("activeSubtitle");
const statusText = document.getElementById("status");

let timeline = [];

/**
 * Handle video upload and receive timeline from server
 */
input.addEventListener("change", async () => {
    const file = input.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("video", file);
    statusText.textContent = "UPLOADING...";

    try {
        const response = await fetch("/upload", { 
            method: "POST", 
            body: formData 
        });

        if (!response.ok) throw new Error('Network response was not ok');

        const data = await response.json();
        
        // Expected JSON: { filename: "vid.mp4", timeline: [{time: 1.5, text: "Hello"}, ...] }
        timeline = data.timeline;
        video.src = `/videos/${data.filename}`;
        
        statusText.textContent = "CONNECTED";
        statusText.style.color = "#38bdf8";
        video.play();
    } catch (err) {
        statusText.textContent = "CONNECTION ERROR";
        statusText.style.color = "#ef4444";
        console.error("Upload Error:", err);
    }
});

/**
 * Toggle Fullscreen for the entire application container
 */
function toggleFullscreen() {
    if (!document.fullscreenElement) {
        appContainer.requestFullscreen().catch(err => {
            alert(`Error attempting to enable full-screen mode: ${err.message}`);
        });
    } else {
        document.exitFullscreen();
    }
}

/**
 * Sync logic: Runs every time the video time changes
 */
video.addEventListener("timeupdate", () => {
    const current = video.currentTime;
    
    // 1. Update the Scrollable Feed (Left side)
    const visibleMessages = timeline.filter(m => m.time <= current);
    
    if (chatBox.children.length !== visibleMessages.length) {
        chatBox.innerHTML = visibleMessages
            .map(m => `<div class="history-item">${m.text}</div>`)
            .join('');
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    // 2. Update the Subtitle Overlay (Bottom of video)
    // Find a message that started in the past, but is less than 3 seconds old
    const currentMsg = timeline.findLast(m => current >= m.time && current < m.time + 3);
    
    if (currentMsg) {
        activeSubtitle.textContent = currentMsg.text;
        activeSubtitle.style.opacity = "1";
    } else {
        activeSubtitle.style.opacity = "0";
    }
});