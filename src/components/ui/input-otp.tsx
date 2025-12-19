'use client';

import * as React from 'react';
import { OTPInput, OTPInputContext } from 'input-otp';
import { cn } from './utils';

function InputOTP({
  className,
  containerClassName,
  ...props
}: React.ComponentProps<typeof OTPInput> & { containerClassName?: string }) {
  return <OTPInput containerClassName={cn('flex items-center gap-2', containerClassName)} className={cn('disabled:cursor-not-allowed', className)} {...props} />;
}
InputOTP.displayName = 'InputOTP';

function InputOTPGroup({ className, ...props }: React.ComponentProps<'div'>) {
  return <div className={cn('flex items-center gap-2', className)} {...props} />;
}
InputOTPGroup.displayName = 'InputOTPGroup';

function InputOTPSlot({ index, className, ...props }: React.ComponentProps<'div'> & { index: number }) {
  const inputOTPContext = React.useContext(OTPInputContext);
  const { char, hasFakeCaret, isActive } = inputOTPContext?.slots[index] ?? {};

  return (
    <div className={cn('relative flex h-9 w-9 items-center justify-center border rounded-md bg-background text-sm transition-all', isActive && 'z-10 ring-2 ring-ring', className)} {...props}>
      {char}
      {hasFakeCaret && <div className="pointer-events-none absolute inset-0 flex items-center justify-center"><div className="h-4 w-px animate-pulse bg-foreground" /></div>}
    </div>
  );
}
InputOTPSlot.displayName = 'InputOTPSlot';

function InputOTPSeparator({ ...props }: React.ComponentProps<'div'>) {
  return <div role="separator" {...props} />;
}
InputOTPSeparator.displayName = 'InputOTPSeparator';

export { InputOTP, InputOTPGroup, InputOTPSlot, InputOTPSeparator };