unit Vlasnik_GlavnaForma;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Objects;

type
  TfrmGlavniMeni = class(TForm)
    Odjava: TButton;
    Termini: TButton;
    Ljubmci: TButton;
    CallCentar: TButton;
    Obavestenja: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure OdjavaClick(Sender: TObject);
    procedure Ljubimci_DodavanjeClick(Sender: TObject);
    procedure TerminiClick(Sender: TObject);
    procedure LjubmciClick(Sender: TObject);
    procedure CallCentarClick(Sender: TObject);
    procedure ObavestenjaClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmGlavniMeni: TfrmGlavniMeni;

implementation

{$R *.fmx}
uses LoginForma, DodavanjeLjubimca, SpisakTermina, SpisakZivotinja, CallCentar, Obavestenja;

procedure TfrmGlavniMeni.CallCentarClick(Sender: TObject);
begin
CCForm1.show;
self.hide;
end;

procedure TfrmGlavniMeni.Ljubimci_DodavanjeClick(Sender: TObject);
begin
frmDodavanjeLjubimca.Show;
self.hide;
end;

procedure TfrmGlavniMeni.LjubmciClick(Sender: TObject);
begin
frmSpisakZivotinja.show;
self.hide;
end;

procedure TfrmGlavniMeni.ObavestenjaClick(Sender: TObject);
begin
frmobavestenja.show;
self.hide;
end;

procedure TfrmGlavniMeni.OdjavaClick(Sender: TObject);
begin
frmLogin.show;
self.hide;
end;

procedure TfrmGlavniMeni.TerminiClick(Sender: TObject);
begin
frmSpisakTermina.show;
self.hide;
end;

end.
