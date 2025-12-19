'use client';

import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from './utils';

const SIDEBAR_COOKIE_NAME = 'sidebar:state';
const SIDEBAR_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
const SIDEBAR_WIDTH = '16rem';
const SIDEBAR_WIDTH_MOBILE = '18rem';
const SIDEBAR_WIDTH_ICON = '3rem';

type SidebarContext = {
  state: 'expanded' | 'collapsed';
  open: boolean;
  setOpen: (open: boolean) => void;
  openMobile: boolean;
  setOpenMobile: (open: boolean) => void;
  isMobile: boolean;
  toggleSidebar: () => void;
};

const SidebarContext = React.createContext<SidebarContext | undefined>(undefined);

function useSidebar() {
  const context = React.useContext(SidebarContext);
  if (!context) {
    throw new Error('useSidebar must be used within a SidebarProvider');
  }
  return context;
}

const SidebarProvider = ({
  defaultOpen = true,
  open: openProp,
  onOpenChange: setOpenProp,
  className,
  children,
  style,
  ...props
}: React.ComponentProps<'div'> & {
  defaultOpen?: boolean;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}) => {
  const [openMobile, setOpenMobile] = React.useState(false);
  const [open, setOpen] = React.useState(openProp ?? defaultOpen);
  const isMobile = true;

  const toggleSidebar = React.useCallback(() => {
    setOpen((prev) => !prev);
  }, []);

  const value: SidebarContext = {
    state: open ? 'expanded' : 'collapsed',
    open,
    setOpen: setOpenProp ?? setOpen,
    openMobile,
    setOpenMobile,
    isMobile,
    toggleSidebar,
  };

  return (
    <SidebarContext.Provider value={value}>
      <div
        style={{
          '--sidebar-width': SIDEBAR_WIDTH,
          '--sidebar-width-icon': SIDEBAR_WIDTH_ICON,
          ...style,
        } as React.CSSProperties}
        className={cn('flex h-full w-full', className)}
        {...props}
      >
        {children}
      </div>
    </SidebarContext.Provider>
  );
};
SidebarProvider.displayName = 'SidebarProvider';

const Sidebar = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement> & { side?: 'left' | 'right' } & VariantProps<typeof sidebarVariants>
>(({ side = 'left', variant = 'sidebar', className, ...props }, ref) => {
  const { toggleSidebar, state, openMobile, setOpenMobile } = useSidebar();

  return (
    <div
      ref={ref}
      className={cn(
        sidebarVariants({ variant, state }),
        side === 'right' && 'order-last',
        className
      )}
      {...props}
    />
  );
});
Sidebar.displayName = 'Sidebar';

const sidebarVariants = cva('h-full bg-sidebar text-sidebar-foreground transition-[width,margin] duration-200 ease-in-out', {
  variants: {
    variant: {
      sidebar: 'w-[--sidebar-width] border-r',
      floating: 'w-[--sidebar-width] border rounded-lg m-2',
      inset: 'w-[--sidebar-width] border-r',
    },
    state: {
      expanded: '',
      collapsed: 'w-[--sidebar-width-icon]',
    },
  },
  defaultVariants: {
    variant: 'sidebar',
    state: 'expanded',
  },
});

const SidebarContent = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn('flex flex-1 flex-col gap-4 overflow-y-auto px-4 py-4', className)} {...props} />
));
SidebarContent.displayName = 'SidebarContent';

const SidebarHeader = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn('flex items-center gap-2 px-4 py-4 border-b', className)} {...props} />
));
SidebarHeader.displayName = 'SidebarHeader';

const SidebarFooter = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn('flex items-center gap-2 px-4 py-4 border-t', className)} {...props} />
));
SidebarFooter.displayName = 'SidebarFooter';

const SidebarTrigger = React.forwardRef<HTMLButtonElement, React.ButtonHTMLAttributes<HTMLButtonElement>>(({ className, ...props }, ref) => (
  <button ref={ref} className={cn('inline-flex items-center justify-center rounded-md p-2 hover:bg-accent', className)} {...props} />
));
SidebarTrigger.displayName = 'SidebarTrigger';

const SidebarRail = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(({ className, ...props }, ref) => (
  <div ref={ref} className={cn('hidden sm:flex absolute left-full top-0 h-full w-4 items-center justify-center group-hover:bg-sidebar-accent', className)} {...props} />
));
SidebarRail.displayName = 'SidebarRail';

export { Sidebar, SidebarProvider, SidebarContent, SidebarHeader, SidebarFooter, SidebarTrigger, SidebarRail, useSidebar };