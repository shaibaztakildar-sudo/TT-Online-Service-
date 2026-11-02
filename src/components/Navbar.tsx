import React, { useState } from 'react';
import { User } from 'firebase/auth';
import {
  FileText,
  User as UserIcon,
  LogOut,
  ShieldCheck,
  Menu,
  X,
  Bell,
  Search,
  ExternalLink,
  PhoneCall,
  LayoutDashboard,
  ClipboardList
} from 'lucide-react';
import { signInWithGoogle, logoutUser, ADMIN_EMAIL } from '../firebase';
import { AppNotification } from '../types';

interface NavbarProps {
  user: User | null;
  activeTab: string;
  setActiveTab: (tab: string) => void;
  notifications: AppNotification[];
  onOpenNotifications: () => void;
  onOpenServiceModal?: (serviceId: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  user,
  activeTab,
  setActiveTab,
  notifications,
  onOpenNotifications
}) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [isSigningIn, setIsSigningIn] = useState(false);

  const isAdmin = user && (user.email || '').toLowerCase() === ADMIN_EMAIL.toLowerCase();
  const unreadCount = notifications.filter(n => !n.read).length;

  const handleSignIn = async () => {
    try {
      setIsSigningIn(true);
      await signInWithGoogle();
    } catch (err: any) {
      console.error(err);
      alert('Google Sign-In was cancelled or failed. Please try again.');
    } finally {
      setIsSigningIn(false);
    }
  };

  const navLinks = [
    { id: 'home', label: 'Home' },
    { id: 'services', label: 'Services' },
    { id: 'track', label: 'Track Application' },
    { id: 'about', label: 'About Us' },
    { id: 'contact', label: 'Contact' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200 shadow-sm">
      {/* Top Advisory Bar */}
      <div className="bg-slate-900 text-slate-300 text-xs py-1.5 px-4 sm:px-8 border-b border-slate-800">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row justify-between items-center gap-1 text-center sm:text-left">
          <div className="flex items-center space-x-2">
            <span className="inline-block w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span>Indian Digital Document Facilitation & Seva Assistance Portal</span>
            <span className="hidden md:inline text-slate-500">|</span>
            <span className="hidden md:inline text-slate-400">All Fees in Indian Rupees (₹)</span>
          </div>
          <div className="flex items-center space-x-4 text-[11px] text-slate-400">
            <a href="tel:+919876543210" className="hover:text-white flex items-center gap-1 transition-colors">
              <PhoneCall className="w-3 h-3 text-emerald-400" />
              <span>Helpline: +91 98765 43210</span>
            </a>
            <span className="text-slate-600">|</span>
            <span className="text-amber-300 font-medium">9 Document Services Available</span>
          </div>
        </div>
      </div>

      {/* Main Navigation */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          
          {/* Logo & Branding */}
          <div 
            onClick={() => { setActiveTab('home'); setMobileMenuOpen(false); }}
            className="flex items-center space-x-3 cursor-pointer group"
          >
            <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-blue-700 via-indigo-700 to-blue-600 flex items-center justify-center text-white shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform duration-200">
              <FileText className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-extrabold text-xl tracking-tight text-slate-900">
                  TT <span className="text-blue-700">Online</span> Services
                </span>
                <span className="bg-blue-100 text-blue-800 text-[10px] font-bold px-1.5 py-0.5 rounded uppercase tracking-wider">
                  Verified
                </span>
              </div>
              <p className="text-[11px] text-slate-500 font-medium hidden sm:block">
                Digital Citizen Assistance & Documentation Hub
              </p>
            </div>
          </div>

          {/* Desktop Nav Items */}
          <nav className="hidden lg:flex items-center space-x-1">
            {navLinks.map((link) => (
              <button
                key={link.id}
                onClick={() => setActiveTab(link.id)}
                className={`px-3.5 py-2 rounded-lg text-sm font-semibold transition-all ${
                  activeTab === link.id
                    ? 'text-blue-700 bg-blue-50 shadow-sm'
                    : 'text-slate-700 hover:text-blue-700 hover:bg-slate-50'
                }`}
              >
                {link.label}
              </button>
            ))}

            {user && (
              <button
                onClick={() => setActiveTab('dashboard')}
                className={`px-3.5 py-2 rounded-lg text-sm font-semibold transition-all flex items-center gap-1.5 ${
                  activeTab === 'dashboard'
                    ? 'text-blue-700 bg-blue-50 shadow-sm'
                    : 'text-slate-700 hover:text-blue-700 hover:bg-slate-50'
                }`}
              >
                <ClipboardList className="w-4 h-4 text-blue-600" />
                <span>My Applications</span>
              </button>
            )}

            {isAdmin && (
              <button
                onClick={() => setActiveTab('admin')}
                className={`px-3.5 py-2 rounded-lg text-sm font-bold transition-all flex items-center gap-1.5 ${
                  activeTab === 'admin'
                    ? 'text-amber-900 bg-amber-100 shadow-sm ring-1 ring-amber-300'
                    : 'text-amber-800 hover:bg-amber-50'
                }`}
              >
                <ShieldCheck className="w-4 h-4 text-amber-600" />
                <span>Owner Admin</span>
              </button>
            )}
          </nav>

          {/* User Controls */}
          <div className="hidden sm:flex items-center space-x-3">
            {user ? (
              <div className="flex items-center space-x-3">
                {/* Notifications Button */}
                <button
                  onClick={onOpenNotifications}
                  className="relative p-2.5 rounded-xl text-slate-600 hover:text-blue-700 hover:bg-slate-100 transition-colors"
                  title="Notifications"
                >
                  <Bell className="w-5 h-5" />
                  {unreadCount > 0 && (
                    <span className="absolute top-1.5 right-1.5 w-4 h-4 bg-red-600 text-white rounded-full text-[10px] font-bold flex items-center justify-center animate-bounce">
                      {unreadCount}
                    </span>
                  )}
                </button>

                {/* Profile Pill */}
                <div className="flex items-center bg-slate-50 border border-slate-200/80 rounded-full pl-2 pr-1.5 py-1">
                  <div className="w-7 h-7 rounded-full bg-blue-600 text-white font-bold flex items-center justify-center text-xs overflow-hidden">
                    {user.photoURL ? (
                      <img src={user.photoURL} alt={user.displayName || 'User'} className="w-full h-full object-cover" />
                    ) : (
                      user.displayName ? user.displayName.charAt(0).toUpperCase() : 'U'
                    )}
                  </div>
                  <div className="mx-2 text-left">
                    <p className="text-xs font-bold text-slate-800 leading-tight max-w-[110px] truncate">
                      {user.displayName || 'Customer'}
                    </p>
                    <p className="text-[10px] text-slate-500 font-medium leading-none">
                      {isAdmin ? 'Owner Admin' : 'Customer'}
                    </p>
                  </div>
                  <button
                    onClick={() => logoutUser()}
                    className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                    title="Sign Out"
                  >
                    <LogOut className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ) : (
              <button
                onClick={handleSignIn}
                disabled={isSigningIn}
                className="flex items-center space-x-2 bg-gradient-to-r from-blue-700 to-indigo-700 hover:from-blue-800 hover:to-indigo-800 text-white text-sm font-semibold px-4 py-2.5 rounded-xl shadow-md shadow-blue-600/20 active:scale-95 transition-all"
              >
                <svg className="w-4 h-4 fill-current" viewBox="0 0 24 24">
                  <path d="M12.24 10.285V13.4h6.887C18.2 16.14 15.645 18 12.24 18c-3.315 0-6-2.685-6-6s2.685-6 6-6c1.47 0 2.81.53 3.86 1.41l2.42-2.42C16.98 3.52 14.77 2.6 12.24 2.6 7.03 2.6 2.8 6.83 2.8 12.04s4.23 9.44 9.44 9.44c5.44 0 9.06-3.82 9.06-9.22 0-.64-.06-1.25-.17-1.975H12.24z" />
                </svg>
                <span>{isSigningIn ? 'Connecting...' : 'Continue with Google'}</span>
              </button>
            )}
          </div>

          {/* Mobile Menu Button */}
          <div className="flex items-center space-x-2 lg:hidden">
            {user && (
              <button
                onClick={onOpenNotifications}
                className="relative p-2 rounded-lg text-slate-600 hover:bg-slate-100"
              >
                <Bell className="w-5 h-5" />
                {unreadCount > 0 && (
                  <span className="absolute top-1 right-1 w-2.5 h-2.5 bg-red-600 rounded-full"></span>
                )}
              </button>
            )}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2.5 rounded-xl text-slate-700 hover:bg-slate-100 focus:outline-none"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="lg:hidden border-t border-slate-200 bg-white px-4 pt-3 pb-6 space-y-2 shadow-xl animate-in slide-in-from-top-2 duration-200">
          <div className="grid grid-cols-2 gap-2 mb-3">
            {navLinks.map((link) => (
              <button
                key={link.id}
                onClick={() => {
                  setActiveTab(link.id);
                  setMobileMenuOpen(false);
                }}
                className={`px-3 py-2.5 rounded-lg text-sm font-semibold text-left transition-colors ${
                  activeTab === link.id
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-700 bg-slate-50 hover:bg-slate-100'
                }`}
              >
                {link.label}
              </button>
            ))}
          </div>

          {user && (
            <div className="pt-2 border-t border-slate-100 space-y-2">
              <button
                onClick={() => {
                  setActiveTab('dashboard');
                  setMobileMenuOpen(false);
                }}
                className={`w-full flex items-center space-x-2 px-3 py-2.5 rounded-lg text-sm font-semibold ${
                  activeTab === 'dashboard'
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-800 bg-blue-50'
                }`}
              >
                <ClipboardList className="w-4 h-4" />
                <span>My Applications & Documents</span>
              </button>

              {isAdmin && (
                <button
                  onClick={() => {
                    setActiveTab('admin');
                    setMobileMenuOpen(false);
                  }}
                  className={`w-full flex items-center space-x-2 px-3 py-2.5 rounded-lg text-sm font-bold ${
                    activeTab === 'admin'
                      ? 'bg-amber-600 text-white'
                      : 'text-amber-900 bg-amber-50 border border-amber-200'
                  }`}
                >
                  <ShieldCheck className="w-4 h-4" />
                  <span>Owner Admin Panel</span>
                </button>
              )}

              <div className="flex items-center justify-between p-3 bg-slate-50 rounded-xl mt-3">
                <div className="flex items-center space-x-2">
                  <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-xs">
                    {user.photoURL ? (
                      <img src={user.photoURL} alt={user.displayName || 'User'} className="w-full h-full rounded-full" />
                    ) : (
                      'U'
                    )}
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-800">{user.displayName || 'Logged In'}</p>
                    <p className="text-[10px] text-slate-500">{user.email}</p>
                  </div>
                </div>
                <button
                  onClick={() => {
                    logoutUser();
                    setMobileMenuOpen(false);
                  }}
                  className="text-xs font-semibold text-red-600 hover:text-red-700 px-3 py-1 bg-red-50 rounded-lg"
                >
                  Sign Out
                </button>
              </div>
            </div>
          )}

          {!user && (
            <div className="pt-3">
              <button
                onClick={() => {
                  handleSignIn();
                  setMobileMenuOpen(false);
                }}
                className="w-full flex items-center justify-center space-x-2 bg-blue-700 hover:bg-blue-800 text-white font-semibold py-3 px-4 rounded-xl shadow-md text-sm"
              >
                <svg className="w-4 h-4 fill-current" viewBox="0 0 24 24">
                  <path d="M12.24 10.285V13.4h6.887C18.2 16.14 15.645 18 12.24 18c-3.315 0-6-2.685-6-6s2.685-6 6-6c1.47 0 2.81.53 3.86 1.41l2.42-2.42C16.98 3.52 14.77 2.6 12.24 2.6 7.03 2.6 2.8 6.83 2.8 12.04s4.23 9.44 9.44 9.44c5.44 0 9.06-3.82 9.06-9.22 0-.64-.06-1.25-.17-1.975H12.24z" />
                </svg>
                <span>Continue with Google</span>
              </button>
            </div>
          )}
        </div>
      )}
    </header>
  );
};
