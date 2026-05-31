"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  Rocket, 
  Search, 
  Target, 
  Clock, 
  CheckCircle2, 
  AlertCircle,
  Briefcase,
  GraduationCap,
  Trophy
} from "lucide-react";
import { useRouter } from "next/navigation";

export default function Dashboard() {
  const [stats, setStats] = useState({
    totalOpportunities: 0,
    activeApplications: 0,
    agentRuns: 0,
    topMatch: 0
  });
  const [recentMatches, setRecentMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    async function fetchDashboardData() {
      try {
        const [opps, apps, runs, matches] = await Promise.all([
          api.get("/opportunities/"),
          api.get("/applications/"),
          api.get("/agents/executions"),
          api.get("/opportunities/matches")
        ]);

        setStats({
          totalOpportunities: opps.data.length,
          activeApplications: apps.data.filter((a: any) => a.status !== 'rejected').length,
          agentRuns: runs.data.length,
          topMatch: matches.data[0]?.score || 0
        });
        setRecentMatches(matches.data.slice(0, 5));
      } catch (error) {
        console.error("Failed to fetch dashboard data", error);
      } finally {
        setLoading(false);
      }
    }
    fetchDashboardData();
  }, []);

  const runDiscovery = async () => {
    try {
      await api.post("/agents/discovery");
      router.push("/agents/runs");
    } catch (error) {
      console.error("Failed to start discovery", error);
    }
  };

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="mx-auto max-w-7xl space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-white">Dashboard</h1>
            <p className="text-muted-foreground">Welcome back, here's what Hermes found for you.</p>
          </div>
          <Button onClick={runDiscovery} className="gap-2">
            <Rocket className="h-4 w-4" />
            Run Hermes Scout
          </Button>
        </div>

        {/* Stats Grid */}
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Scouted Opportunities</CardTitle>
              <Search className="h-4 w-4 text-primary" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalOpportunities}</div>
              <p className="text-xs text-muted-foreground">+12 from last week</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Active Applications</CardTitle>
              <Target className="h-4 w-4 text-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.activeApplications}</div>
              <p className="text-xs text-muted-foreground">4 tasks pending</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Agent Workflows</CardTitle>
              <Clock className="h-4 w-4 text-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.agentRuns}</div>
              <p className="text-xs text-muted-foreground">Last run 2h ago</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Best Match Score</CardTitle>
              <Trophy className="h-4 w-4 text-primary" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{(stats.topMatch * 100).toFixed(0)}%</div>
              <p className="text-xs text-muted-foreground">Found in "Senior AI Engineer"</p>
            </CardContent>
          </Card>
        </div>

        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-7">
          {/* Recent Matches */}
          <Card className="lg:col-span-4">
            <CardHeader>
              <CardTitle>Recommended Opportunities</CardTitle>
              <CardDescription>AI-ranked matches based on your profile.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentMatches.length === 0 ? (
                  <p className="text-center text-muted-foreground py-10">No matches found yet. Run Hermes to discover opportunities.</p>
                ) : (
                  recentMatches.map((match: any) => (
                    <div key={match.id} className="flex items-center justify-between p-4 rounded-lg border border-white/5 bg-white/5 hover:bg-white/10 transition-colors">
                      <div className="flex items-center gap-4">
                        <div className="p-2 rounded-full bg-primary/10">
                          <Briefcase className="h-5 w-5 text-primary" />
                        </div>
                        <div>
                          <p className="font-semibold">{match.opportunity.title}</p>
                          <p className="text-sm text-muted-foreground">{match.opportunity.organization}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-right">
                          <p className="text-lg font-bold text-primary">{(match.score * 100).toFixed(0)}%</p>
                          <p className="text-xs text-muted-foreground">Match Score</p>
                        </div>
                        <Button size="sm" variant="outline" onClick={() => router.push(`/opportunities/${match.opportunity.id}`)}>
                          View
                        </Button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </CardContent>
          </Card>

          {/* Quick Actions / Agent Status */}
          <Card className="lg:col-span-3">
            <CardHeader>
              <CardTitle>Agent Status</CardTitle>
              <CardDescription>Hermes Agent live activity logs.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="mt-1">
                    <CheckCircle2 className="h-4 w-4 text-success" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">Finished researching 12 scholarships</p>
                    <p className="text-xs text-muted-foreground">15 minutes ago</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1 text-primary">
                    <Search className="h-4 w-4 animate-pulse" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">Analyzing remote job requirements</p>
                    <p className="text-xs text-muted-foreground">Just now</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1">
                    <AlertCircle className="h-4 w-4 text-warning" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">Deadline approaching for "YC W2025"</p>
                    <p className="text-xs text-muted-foreground">Tomorrow</p>
                  </div>
                </div>
              </div>
              <Button variant="secondary" className="w-full mt-6" onClick={() => router.push("/agents/runs")}>
                View Full Logs
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
