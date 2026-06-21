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
            // href updated via fetch below
        } else if (os === 'Linux') {
            heroText.textContent = 'Copy Linux Command';
            heroBtn.href = 'javascript:void(0)';
            heroBtn.addEventListener('click', (e) => {
                e.preventDefault();
                navigator.clipboard.writeText('curl -sL https://raw.githubusercontent.com/JamieLittle16/LogiK/main/src/main/install.sh | bash');
                heroText.textContent = 'Copied to Clipboard!';
                setTimeout(() => heroText.textContent = 'Copy Linux Command', 2000);
            });
        } else if (os === 'MacOS') {
            heroText.textContent = 'Download for macOS';
            // href updated via fetch below
        }

        // Dynamically fetch actual asset URLs from GitHub Releases
        fetch('https://api.github.com/repos/JamieLittle16/LogiK/releases/latest')
            .then(response => response.json())
            .then(data => {
                if (!data.assets) return;
                
                let jarUrl = 'https://github.com/JamieLittle16/LogiK/releases/latest';
                let msiUrl = 'https://github.com/JamieLittle16/LogiK/releases/latest';
                
                data.assets.forEach(asset => {
                    if (asset.name.endsWith('.jar')) jarUrl = asset.browser_download_url;
                    if (asset.name.endsWith('.msi')) msiUrl = asset.browser_download_url;
                });

                // Update Hero Button
                if (os === 'Windows') heroBtn.href = msiUrl;
                if (os === 'MacOS') heroBtn.href = jarUrl;

                // Update Bottom Grid Buttons
                const winBtn = document.querySelector('.download-btn.windows');
                const uniBtn = document.querySelector('.download-btn.universal');
                if (winBtn) winBtn.href = msiUrl;
                if (uniBtn) uniBtn.href = jarUrl;
            })
            .catch(err => console.error('Failed to fetch release assets:', err));
    }
});
