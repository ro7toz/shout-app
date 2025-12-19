'use client';

import * as React from 'react';
import * as RechartsPrimitive from 'recharts';
import { cn } from './utils';

const COLORS = ['#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981', '#6366f1', '#06b6d4'];

const ChartContainer = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement> & { config: Record<string, { label: string; color?: string }> }
>(({ className, children, ...props }, ref) => (
  <div ref={ref} className={cn('w-full h-[400px]', className)} {...props}>
    {children}
  </div>
));
ChartContainer.displayName = 'ChartContainer';

const ChartTooltip = RechartsPrimitive.Tooltip;
const ChartLegend = RechartsPrimitive.Legend;
const ChartResponsiveContainer = RechartsPrimitive.ResponsiveContainer;
const ChartLineChart = RechartsPrimitive.LineChart;
const ChartBarChart = RechartsPrimitive.BarChart;
const ChartLine = RechartsPrimitive.Line;
const ChartBar = RechartsPrimitive.Bar;
const ChartXAxis = RechartsPrimitive.XAxis;
const ChartYAxis = RechartsPrimitive.YAxis;
const ChartCartesianGrid = RechartsPrimitive.CartesianGrid;

export { COLORS, ChartContainer, ChartTooltip, ChartLegend, ChartResponsiveContainer, ChartLineChart, ChartBarChart, ChartLine, ChartBar, ChartXAxis, ChartYAxis, ChartCartesianGrid };