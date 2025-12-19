import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useData } from '../contexts/DataContext';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { TrendingUp, Users, MessageSquare, Star } from 'lucide-react';

interface StatCardProps {
  label: string;
  value: string | number;
  change: string;
  icon: React.ReactNode;
}

const StatCard: React.FC<StatCardProps> = ({ label, value, change, icon }) => (
  <Card>
    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
      <CardTitle className="text-sm font-medium">{label}</CardTitle>
      {icon}
    </CardHeader>
    <CardContent>
      <div className="text-2xl font-bold">{value}</div>
      <p className="text-xs text-muted-foreground">{change}</p>
    </CardContent>
  </Card>
);

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const { exchanges } = useData();
  const [showPricing, setShowPricing] = useState(false);

  const isProUser = user?.planType === 'pro';

  return (
    <>
      <div className="min-h-screen bg-background p-4 md:p-8">
        <div className="max-w-6xl mx-auto space-y-6">
          <div className="flex justify-between items-center">
            <h1 className="text-4xl font-bold">Dashboard</h1>
            {!isProUser && (
              <Button onClick={() => setShowPricing(true)}>
                Upgrade to Pro
              </Button>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <StatCard
              label="Total Exchanges"
              value={exchanges.length}
              change="+4 from last month"
              icon={<Users className="h-4 w-4 text-muted-foreground" />}
            />
            <StatCard
              label="Completion Rate"
              value="75%"
              change="+12.5% from last month"
              icon={<TrendingUp className="h-4 w-4 text-muted-foreground" />}
            />
            <StatCard
              label="Requests Sent"
              value={user?.dailyRequestsSent || 0}
              change="+8.2% from last month"
              icon={<MessageSquare className="h-4 w-4 text-muted-foreground" />}
            />
            <StatCard
              label="Average Rating"
              value="4.8"
              change="+15.3% from last month"
              icon={<Star className="h-4 w-4 text-muted-foreground" />}
            />
          </div>
        </div>
      </div>
    </>
  );
};