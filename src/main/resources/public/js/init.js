document.addEventListener('DOMContentLoaded', () => {
    // Course management
    const addCourseBtn = document.getElementById('btnAddCourse');
    const courseForm = document.getElementById('courseForm');
    const courseModal = document.getElementById('courseModal');
    const closeCourseModal = document.getElementById('closeCourseModal');

    if (addCourseBtn) {
        addCourseBtn.addEventListener('click', () => {
            courseModal.style.display = 'block';
        });
    }

    if (closeCourseModal) {
        closeCourseModal.addEventListener('click', () => {
            courseModal.style.display = 'none';
            courseForm.reset();
        });
    }

    if (courseForm) {
        courseForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(courseForm);
            const courseData = {
                courseCode: formData.get('courseCode'),
                courseName: formData.get('courseName'),
                facultyUsername: formData.get('facultyUsername'),
                maxStudents: parseInt(formData.get('maxStudents'))
            };

            const created = await createCourse(courseData);
            if (created) {
                courseModal.style.display = 'none';
                courseForm.reset();
                await loadCourses();
            }
        });
    }

    // Room management
    const addRoomBtn = document.getElementById('btnAddRoom');
    const roomForm = document.getElementById('roomForm');
    const roomModal = document.getElementById('roomModal');
    const closeRoomModal = document.getElementById('closeRoomModal');

    if (addRoomBtn) {
        addRoomBtn.addEventListener('click', () => {
            roomModal.style.display = 'block';
        });
    }

    if (closeRoomModal) {
        closeRoomModal.addEventListener('click', () => {
            roomModal.style.display = 'none';
            roomForm.reset();
        });
    }

    if (roomForm) {
        roomForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(roomForm);
            const roomData = {
                name: formData.get('roomName'),
                capacity: parseInt(formData.get('capacity'))
            };

            const created = await createRoom(roomData);
            if (created) {
                roomModal.style.display = 'none';
                roomForm.reset();
                await loadRooms();
            }
        });
    }

    // Close modals when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === courseModal) {
            courseModal.style.display = 'none';
            courseForm.reset();
        }
        if (e.target === roomModal) {
            roomModal.style.display = 'none';
            roomForm.reset();
        }
    });

    // Load initial data
    loadCourses();
    loadRooms();
});