document.addEventListener('DOMContentLoaded', () => {
    // Navbar scroll effect
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                // Adjust offset for navbar
                const navbarHeight = navbar.offsetHeight;
                const targetPosition = targetElement.getBoundingClientRect().top + window.pageYOffset - navbarHeight;
                
                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });

    // Reveal animations on scroll
    const revealElements = document.querySelectorAll('.reveal');
    
    const revealOnScroll = () => {
        const windowHeight = window.innerHeight;
        const revealPoint = 150;
        
        revealElements.forEach(element => {
            const elementTop = element.getBoundingClientRect().top;
            
            if (elementTop < windowHeight - revealPoint) {
                element.classList.add('active');
            }
        });
    };
    
    // Trigger once on load
    revealOnScroll();
    
    // Trigger on scroll
    window.addEventListener('scroll', revealOnScroll);

    // OS Detection for Download Button
    const heroBtn = document.getElementById('hero-dl-btn');
    const heroText = document.getElementById('hero-dl-text');
    
    if (heroBtn && heroText) {
        const platform = navigator.platform.toLowerCase();
        const userAgent = navigator.userAgent.toLowerCase();
        
        let os = 'Unknown';
        if (platform.includes('win')) os = 'Windows';
        else if (platform.includes('mac')) os = 'MacOS';
        else if (platform.includes('linux')) os = 'Linux';
        
        if (os === 'Windows') {
            heroText.textContent = 'Download for Windows';
            heroBtn.href = 'https://github.com/JamieLittle16/LogiK/releases/latest/download/LogiK.msi';
        } else if (os === 'Linux') {
            heroText.textContent = 'Download for Linux';
            heroBtn.href = '#download'; // Scroll to the linux curl command
        } else if (os === 'MacOS') {
            heroText.textContent = 'Download for macOS';
            heroBtn.href = 'https://github.com/JamieLittle16/LogiK/releases/latest/download/LogiK.jar';
        }
    }
});
