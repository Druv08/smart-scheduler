document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("rooms-container");

    try {
        const response = await fetch("/api/rooms");
        if (!response.ok) throw new Error("Failed to fetch rooms");

        const data = await response.json();
        container.innerHTML = "";

        if (!data.rooms || data.rooms.length === 0) {
            container.innerHTML = `
                <div class="text-center">
                    <p class="text-gray-400 mb-4">No rooms found.</p>
                    <button onclick="location.href='/add-room.html'" 
                            class="bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded">
                        Add Room
                    </button>
                </div>`;
            return;
        }

        data.rooms.forEach(room => {
            const card = document.createElement("div");
            card.className = 
                "flex justify-between items-center bg-gray-700 rounded-lg p-4 hover:bg-gray-600 transition";

            card.innerHTML = `
                <div>
                    <p class="font-semibold text-lg">${room.room_name}</p>
                    <p class="text-sm text-gray-400">Capacity: ${room.capacity}</p>
                </div>
                <div class="flex space-x-2">
                    <button onclick="editRoom(${room.id})" 
                            class="text-blue-400 hover:text-blue-300">
                        Edit
                    </button>
                    <button onclick="deleteRoom(${room.id})" 
                            class="text-red-400 hover:text-red-300">
                        Delete
                    </button>
                </div>
            `;

            container.appendChild(card);
        });

        // Add "Add Room" button at the bottom
        const addButton = document.createElement("div");
        addButton.className = "text-center mt-6";
        addButton.innerHTML = `
            <button onclick="location.href='/add-room.html'" 
                    class="bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded">
                Add Room
            </button>
        `;
        container.appendChild(addButton);

    } catch (err) {
        console.error("Error loading rooms:", err);
        container.innerHTML = `
            <p class="text-red-400 text-center">
                Error loading rooms. Please try again later.
            </p>`;
    }
});

async function loadRooms() {
  const res = await api('/api/rooms');
  if (!res.success) return toast(res.error || 'Failed to load rooms','error');
  const body = document.getElementById('roomsBody');
  body.innerHTML = '';
  (res.rooms || []).forEach(r => {
    const tr = document.createElement('tr');
    tr.className = 'hover:bg-white/5';
    tr.innerHTML = `
      <td class="px-4 py-3 text-slate-400">${r.id ?? ''}</td>
      <td class="px-4 py-3 font-semibold">${r.roomName || r.room_name}</td>
      <td class="px-4 py-3">${r.capacity ?? ''}</td>`;
    body.appendChild(tr);
  });
}

document.getElementById('createRoomForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const payload = {
    room_name: document.getElementById('r-name').value.trim(),
    capacity: Number(document.getElementById('r-cap').value)
  };
  const res = await api('/api/rooms', 'POST', payload);
  const msg = document.getElementById('r-msg');
  if (res.success) {
    msg.textContent = 'Room created ✅'; msg.className='text-emerald-400';
    e.target.reset();
    loadRooms();
  } else {
    msg.textContent = res.error || 'Failed to create room'; msg.className='text-red-400';
  }
});

// Function stubs for edit and delete - implement these later
function editRoom(id) {
    console.log("Edit room:", id);
    // TODO: Implement room editing
}

function deleteRoom(id) {
    console.log("Delete room:", id);
    // TODO: Implement room deletion
}

loadRooms();