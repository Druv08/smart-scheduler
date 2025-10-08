document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("courses-container");
    const addCourseBtn = document.getElementById("add-course-btn");
    const modal = document.getElementById("add-course-modal");
    const form = document.getElementById("add-course-form");
    const cancelBtn = document.getElementById("cancel-course-btn");

    // Load existing courses
    async function loadCourses() {
        try {
            const res = await fetch("/api/courses");
            const data = await res.json();

            if (!data.success) {
                container.innerHTML = `<p class="text-red-500 text-center">${data.error || 'Failed to load courses'}</p>`;
                return;
            }

            const courses = data.courses;
            if (!courses || courses.length === 0) {
                container.innerHTML = `<p class="text-gray-400 text-center">No courses available yet.</p>`;
                return;
            }

            const table = document.createElement("table");
            table.className = "min-w-full divide-y divide-gray-700";
            table.innerHTML = `
                <thead>
                    <tr class="bg-gray-800">
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Code</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Course Name</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Faculty</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Max Students</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-700">
                    ${courses.map(course => `
                        <tr class="hover:bg-gray-700/50">
                            <td class="px-6 py-4">${course.courseCode}</td>
                            <td class="px-6 py-4">${course.courseName}</td>
                            <td class="px-6 py-4">${course.facultyUsername}</td>
                            <td class="px-6 py-4">${course.maxStudents}</td>
                        </tr>
                    `).join('')}
                </tbody>
            `;

            container.innerHTML = '';
            container.appendChild(table);
        } catch (err) {
            console.error("Error loading courses:", err);
            container.innerHTML = `<p class="text-red-500 text-center">Failed to load courses. Please try again.</p>`;
        }
    }

    // Show modal
    addCourseBtn?.addEventListener("click", () => {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    });

    // Hide modal
    cancelBtn?.addEventListener("click", () => {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        form.reset();
    });

    // Handle form submission
    form?.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        
        try {
            const res = await fetch("/api/courses", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    course_code: formData.get('courseCode'),
                    course_name: formData.get('courseName'),
                    faculty_username: formData.get('facultyUsername'),
                    max_students: parseInt(formData.get('maxStudents'))
                })
            });

            const data = await res.json();
            if (data.success) {
                modal.classList.add('hidden');
                modal.classList.remove('flex');
                form.reset();
                await loadCourses();
            } else {
                alert('Error: ' + (data.error || 'Failed to add course'));
            }
        } catch (err) {
            console.error("Error submitting course:", err);
            alert("Failed to add course. Please try again.");
        }
    });

    // Load courses on page load
    await loadCourses();
});