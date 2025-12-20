import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, Phone, Mail, Instagram, Linkedin, Facebook } from 'lucide-react';

const Footer = () => (
  <footer className="bg-gray-50 border-t mt-auto">
    <div className="container mx-auto px-4 py-12">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h3 className="font-semibold mb-4">About</h3>
          <div className="space-y-2 text-sm text-gray-600">
            <Link to="/terms" className="block hover:text-purple-600 transition">Terms & Conditions</Link>
            <Link to="/privacy" className="block hover:text-purple-600 transition">Privacy Policy</Link>
            <Link to="/refund" className="block hover:text-purple-600 transition">Refund Policy</Link>
          </div>
        </div>
        <div>
          <h3 className="font-semibold mb-4">Contact</h3>
          <div className="space-y-2 text-sm text-gray-600">
            <div className="flex items-center gap-2"><MapPin className="w-4 h-4" /><span>Poonam Colony, Kota (Rajasthan)</span></div>
            <div className="flex items-center gap-2"><Phone className="w-4 h-4" /><span>+91 9509103148</span></div>
            <a href="mailto:tushkinit@gmail.com" className="flex items-center gap-2 hover:text-purple-600 transition">
              <Mail className="w-4 h-4" /><span>tushkinit@gmail.com</span>
            </a>
            <div className="flex gap-4 mt-4">
              <a href="https://instagram.com/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Instagram className="w-5 h-5" />
              </a>
              <a href="https://linkedin.com/company/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Linkedin className="w-5 h-5" />
              </a>
              <a href="https://facebook.com/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Facebook className="w-5 h-5" />
              </a>
            </div>
          </div>
        </div>
      </div>
      <div className="text-center text-sm text-gray-600 mt-8 pt-8 border-t">
        © 2024 ShoutX. All rights reserved.
      </div>
    </div>
  </footer>
);

export default Footer;
