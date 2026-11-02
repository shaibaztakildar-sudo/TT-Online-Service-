import { initializeApp, getApps } from 'firebase/app';
import {
  getAuth,
  GoogleAuthProvider,
  signInWithPopup,
  signOut as firebaseSignOut,
  onAuthStateChanged,
  User
} from 'firebase/auth';
import {
  initializeFirestore,
  getFirestore,
  collection,
  doc,
  setDoc,
  getDoc,
  updateDoc,
  query,
  where,
  orderBy,
  onSnapshot,
  serverTimestamp,
  Firestore
} from 'firebase/firestore';

export const ADMIN_EMAIL = 'shaibaztakildar@gmail.com';

const firebaseConfig = {
  apiKey: "AIzaSyDwMVpboDwZ8T35ttlEo8IOfyFnaYQ1LMg",
  authDomain: "gen-lang-client-0796900401.firebaseapp.com",
  projectId: "gen-lang-client-0796900401",
  storageBucket: "gen-lang-client-0796900401.firebasestorage.app",
  messagingSenderId: "665264457777",
  appId: "1:665264457777:web:d57112eb76850d1f162d10"
};

// Initialize app if not already initialized
export const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApps()[0];
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });

// Initialize Firestore with custom database ID
let dbInstance: Firestore;
try {
  dbInstance = initializeFirestore(app, {}, 'ai-studio-android-ttonline-047e739f-f599-4f8d-a2bb-831e9309b220');
} catch (e) {
  dbInstance = getFirestore(app, 'ai-studio-android-ttonline-047e739f-f599-4f8d-a2bb-831e9309b220');
}

export const db = dbInstance;

/**
 * Sign in with Google Popup
 */
export async function signInWithGoogle() {
  try {
    const result = await signInWithPopup(auth, googleProvider);
    const user = result.user;
    if (user) {
      // Sync or create user in Firestore users collection
      const userDocRef = doc(db, 'users', user.uid);
      const isOwner = (user.email || '').toLowerCase() === ADMIN_EMAIL.toLowerCase();
      await setDoc(userDocRef, {
        id: user.uid,
        email: user.email || '',
        name: user.displayName || 'User',
        phone: user.phoneNumber || '',
        role: isOwner ? 'admin' : 'customer',
        updatedAt: serverTimestamp()
      }, { merge: true });
    }
    return user;
  } catch (error: any) {
    console.error('Error signing in with Google:', error);
    throw error;
  }
}

/**
 * Sign out
 */
export async function logoutUser() {
  return await firebaseSignOut(auth);
}

export {
  collection,
  doc,
  setDoc,
  getDoc,
  updateDoc,
  query,
  where,
  orderBy,
  onSnapshot,
  serverTimestamp,
  onAuthStateChanged
};
export type { User };
