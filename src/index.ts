// UI Components
export { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from './components/ui/accordion';
export { Badge, badgeVariants } from './components/ui/badge';
export { Button, buttonVariants } from './components/ui/button';
export { Card, CardHeader, CardFooter, CardTitle, CardDescription, CardContent } from './components/ui/card';
export { Input } from './components/ui/input';
export { Label } from './components/ui/label';
export { Textarea } from './components/ui/textarea';
export { Checkbox } from './components/ui/checkbox';

// Contexts
export { AuthProvider, useAuth, type User, type MediaItem } from './contexts/AuthContext';
export { DataProvider, useData, type ShoutoutUser, type ShoutoutRequest, type Exchange, type Notification } from './contexts/DataContext';

// Pages
export { DashboardPage } from './pages/DashboardPage';
export { ProfilePage } from './pages/ProfilePage';
export { TermsPage } from './pages/TermsPage';

// Hooks
export { useIsMobile } from './hooks/use-mobile';

// Utils
export { cn } from './components/ui/utils';